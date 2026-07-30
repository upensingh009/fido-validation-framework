import { test, expect } from '@playwright/test';
import axios from 'axios';

function b64uToUint8Array(b64u: string) {
  // base64url to base64
  const b64 = b64u.replace(/-/g, '+').replace(/_/g, '/');
  const pad = b64.length % 4;
  const b64p = b64 + (pad ? '='.repeat(4 - pad) : '');
  const raw = Buffer.from(b64p, 'base64');
  return new Uint8Array(raw);
}

async function makeCredentialOnPage(page, options: any) {
  // convert challenge and user.id to Uint8Array
  options.publicKey.challenge = b64uToUint8Array(options.publicKey.challenge);
  if (options.publicKey.user && options.publicKey.user.id) {
    options.publicKey.user.id = b64uToUint8Array(options.publicKey.user.id);
  }
  // convert any excludeCredentials id fields
  if (Array.isArray(options.publicKey.excludeCredentials)) {
    options.publicKey.excludeCredentials = options.publicKey.excludeCredentials.map((c) => ({
      ...c,
      id: b64uToUint8Array(c.id)
    }));
  }
  // call navigator.credentials.create in page context
  const result = await page.evaluate(async (opts) => {
    const cred = await navigator.credentials.create(opts);
    const rawId = cred.rawId ? Array.from(new Uint8Array(cred.rawId)) : null;
    const response = cred.response;
    const clientDataJSON = response.clientDataJSON ? Array.from(new Uint8Array(response.clientDataJSON)) : null;
    const attestationObject = response.attestationObject ? Array.from(new Uint8Array(response.attestationObject)) : null;
    return { id: cred.id, rawId, clientDataJSON, attestationObject, type: cred.type };
  }, { publicKey: options.publicKey });
  return result;
}

async function getMakeCredentialOptions(serverUrl: string, path: string, userId: string) {
  const url = serverUrl + path;
  const resp = await axios.post(url, { userId });
  return resp.data;
}

async function submitCredentialToServer(serverUrl: string, path: string, credential: any) {
  const url = serverUrl + path;
  const payload = {
    id: credential.id,
    rawId: Buffer.from(credential.rawId).toString('base64url'),
    type: credential.type,
    response: {
      clientDataJSON: Buffer.from(credential.clientDataJSON).toString('base64url'),
      attestationObject: Buffer.from(credential.attestationObject).toString('base64url')
    }
  };
  const resp = await axios.post(url, payload);
  return resp;
}

// Read env vars
const SERVER_URL = process.env.SERVER_URL || 'https://localhost:8443';
const REGISTER_OPTIONS_PATH = process.env.REGISTER_OPTIONS_PATH || '/webauthn/register/options';
const SUBMIT_CREDENTIAL_PATH = process.env.SUBMIT_CREDENTIAL_PATH || '/webauthn/register/submit';

test('register then authenticate using virtual authenticator', async ({ page, context }) => {
  // start CDP session and enable WebAuthn
  const client = await context.newCDPSession(page);
  await client.send('WebAuthn.enable');
  const authenticatorId = await client.send('WebAuthn.addVirtualAuthenticator', {
    options: {
      protocol: 'u2f',
      transport: 'usb',
      hasResidentKey: false,
      hasUserVerification: false,
      isUserVerified: false
    }
  });

  // fetch makeCredential options from server
  const options = await getMakeCredentialOptions(SERVER_URL, REGISTER_OPTIONS_PATH, 'playwright-user');

  // navigate to server origin so navigator.credentials is available for same-origin
  await page.goto(SERVER_URL);

  // create credential
  const credential = await makeCredentialOnPage(page, options);

  // submit to server
  const resp = await submitCredentialToServer(SERVER_URL, SUBMIT_CREDENTIAL_PATH, credential);
  expect(resp.status).toBe(200);

  // cleanup: remove authenticator
  await client.send('WebAuthn.removeVirtualAuthenticator', { authenticatorId });
});
