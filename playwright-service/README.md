# Playwright Service

This small Express service launches a headless Chromium, adds a virtual authenticator, calls navigator.credentials.create() with the provided publicKey options, and returns the created credential JSON.

Install:

cd playwright-service
npm install

Run:

PORT=3001 npm start

Request format (POST /make-credential):
{
  "serverUrl": "https://your-fido-server:443",   // origin to navigate to
  "publicKeyOptions": { ... }                    // the 'publicKey' options object returned by your server register-options endpoint
}

Response:
{
  "credential": {
    "id": "...",
    "rawId": "base64url...",
    "type": "public-key",
    "response": {
      "clientDataJSON": "base64url...",
      "attestationObject": "base64url..."
    }
  }
}

Notes:
- The page must be able to navigate to serverUrl and be same-origin for navigator.credentials to succeed.
- If your server requires a session cookie or auth to obtain register options, modify the service to set the cookie before calling create.
