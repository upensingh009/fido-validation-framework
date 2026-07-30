const express = require('express');
const bodyParser = require('body-parser');
const { chromium } = require('playwright');

async function createCredential(page, publicKeyOptions) {
  // On the page convert base64url fields into ArrayBuffers and call navigator.credentials.create
  const result = await page.evaluate(async (opts) => {
    function b64uToUint8Array(b64u) {
      const b64 = b64u.replace(/-/g, '+').replace(/_/g, '/');
      const pad = b64.length % 4;
      const padded = b64 + (pad ? '='.repeat(4 - pad) : '');
      const raw = atob(padded);
      const arr = new Uint8Array(raw.length);
      for (let i = 0; i < raw.length; ++i) arr[i] = raw.charCodeAt(i);
      return arr;
    }

    if (opts.challenge && typeof opts.challenge === 'string') {
      opts.challenge = b64uToUint8Array(opts.challenge);
    }
    if (opts.user && opts.user.id && typeof opts.user.id === 'string') {
      opts.user.id = b64uToUint8Array(opts.user.id);
    }
    if (Array.isArray(opts.excludeCredentials)) {
      opts.excludeCredentials = opts.excludeCredentials.map(c => ({
        ...c,
        id: b64uToUint8Array(c.id)
      }));
    }

    const cred = await navigator.credentials.create({ publicKey: opts });

    const rawId = cred.rawId ? new Uint8Array(cred.rawId) : null;
    const clientDataJSON = cred.response && cred.response.clientDataJSON ? new Uint8Array(cred.response.clientDataJSON) : null;
    const attestationObject = cred.response && cred.response.attestationObject ? new Uint8Array(cred.response.attestationObject) : null;

    function uint8ToBase64Url(u8) {
      if (!u8) return null;
      let binary = '';
      for (let i = 0; i < u8.byteLength; i++) binary += String.fromCharCode(u8[i]);
      const b64 = btoa(binary);
      return b64.replace(/\+/g,'-').replace(/\//g,'_').replace(/=+$/,'');
    }

    return {
      id: cred.id,
      rawId: uint8ToBase64Url(rawId),
      type: cred.type,
      response: {
        clientDataJSON: uint8ToBase64Url(clientDataJSON),
        attestationObject: uint8ToBase64Url(attestationObject)
      }
    };
  }, publicKeyOptions);

  return result;
}

async function getAssertion(page, publicKeyOptions) {
  const result = await page.evaluate(async (opts) => {
    function b64uToUint8Array(b64u) {
      const b64 = b64u.replace(/-/g, '+').replace(/_/g, '/');
      const pad = b64.length % 4;
      const padded = b64 + (pad ? '='.repeat(4 - pad) : '');
      const raw = atob(padded);
      const arr = new Uint8Array(raw.length);
      for (let i = 0; i < raw.length; ++i) arr[i] = raw.charCodeAt(i);
      return arr;
    }

    if (opts.challenge && typeof opts.challenge === 'string') {
      opts.challenge = b64uToUint8Array(opts.challenge);
    }
    if (Array.isArray(opts.allowCredentials)) {
      opts.allowCredentials = opts.allowCredentials.map(c => ({
        ...c,
        id: b64uToUint8Array(c.id)
      }));
    }

    const assertion = await navigator.credentials.get({ publicKey: opts });

    const rawId = assertion.rawId ? new Uint8Array(assertion.rawId) : null;
    const clientDataJSON = assertion.response && assertion.response.clientDataJSON ? new Uint8Array(assertion.response.clientDataJSON) : null;
    const authenticatorData = assertion.response && assertion.response.authenticatorData ? new Uint8Array(assertion.response.authenticatorData) : null;
    const signature = assertion.response && assertion.response.signature ? new Uint8Array(assertion.response.signature) : null;
    const userHandle = assertion.response && assertion.response.userHandle ? new Uint8Array(assertion.response.userHandle) : null;

    function uint8ToBase64Url(u8) {
      if (!u8) return null;
      let binary = '';
      for (let i = 0; i < u8.byteLength; i++) binary += String.fromCharCode(u8[i]);
      const b64 = btoa(binary);
      return b64.replace(/\+/g,'-').replace(/\//g,'_').replace(/=+$/,'');
    }

    return {
      id: assertion.id,
      rawId: uint8ToBase64Url(rawId),
      type: assertion.type,
      response: {
        clientDataJSON: uint8ToBase64Url(clientDataJSON),
        authenticatorData: uint8ToBase64Url(authenticatorData),
        signature: uint8ToBase64Url(signature),
        userHandle: uint8ToBase64Url(userHandle)
      }
    };
  }, publicKeyOptions);

  return result;
}

async function startService() {
  const express = require('express');
  const bodyParser = require('body-parser');
  const app = express();
  app.use(bodyParser.json({ limit: '20mb' }));

  const browser = await chromium.launch({ headless: true, args: ['--disable-web-security'] });

  app.post('/make-credential', async (req, res) => {
    let context, page, client, authenticatorId;
    try {
      const { serverUrl, publicKeyOptions } = req.body;
      if (!serverUrl || !publicKeyOptions) {
        res.status(400).json({ error: 'serverUrl and publicKeyOptions required' });
        return;
      }

      context = await browser.newContext();
      page = await context.newPage();

      // Navigate to server origin (must be same origin for credentials)
      await page.goto(serverUrl, { waitUntil: 'domcontentloaded', timeout: 15000 });

      // Setup CDP WebAuthn
      client = await context.newCDPSession(page);
      await client.send('WebAuthn.enable');
      const addResp = await client.send('WebAuthn.addVirtualAuthenticator', {
        options: {
          protocol: 'u2f',
          transport: 'usb',
          hasResidentKey: false,
          hasUserVerification: false,
          isUserVerified: false
        }
      });
      authenticatorId = addResp.authenticatorId;

      const credential = await createCredential(page, publicKeyOptions);

      res.json({ credential });
    } catch (err) {
      console.error('make-credential error', err);
      res.status(500).json({ error: err.message });
    } finally {
      try { if (client && authenticatorId) await client.send('WebAuthn.removeVirtualAuthenticator', { authenticatorId }); } catch (e) {}
      try { if (page) await page.close(); } catch (e) {}
      try { if (context) await context.close(); } catch (e) {}
    }
  });

  app.post('/get-assertion', async (req, res) => {
    let context, page, client, authenticatorId;
    try {
      const { serverUrl, publicKeyOptions } = req.body;
      if (!serverUrl || !publicKeyOptions) {
        res.status(400).json({ error: 'serverUrl and publicKeyOptions required' });
        return;
      }

      context = await browser.newContext();
      page = await context.newPage();

      await page.goto(serverUrl, { waitUntil: 'domcontentloaded', timeout: 15000 });

      client = await context.newCDPSession(page);
      await client.send('WebAuthn.enable');
      const addResp = await client.send('WebAuthn.addVirtualAuthenticator', {
        options: {
          protocol: 'u2f',
          transport: 'usb',
          hasResidentKey: false,
          hasUserVerification: false,
          isUserVerified: false
        }
      });
      authenticatorId = addResp.authenticatorId;

      const assertion = await getAssertion(page, publicKeyOptions);

      res.json({ assertion });
    } catch (err) {
      console.error('get-assertion error', err);
      res.status(500).json({ error: err.message });
    } finally {
      try { if (client && authenticatorId) await client.send('WebAuthn.removeVirtualAuthenticator', { authenticatorId }); } catch (e) {}
      try { if (page) await page.close(); } catch (e) {}
      try { if (context) await context.close(); } catch (e) {}
    }
  });

  const port = process.env.PORT || 3001;
  app.listen(port, () => console.log(`Playwright service listening on http://localhost:${port}`));

  process.on('SIGINT', async () => {
    try { await browser.close(); } catch (e) {}
    process.exit(0);
  });
}

startService().catch(e => { console.error(e); process.exit(1); });
