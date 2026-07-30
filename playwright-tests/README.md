# Playwright tests

This folder contains a Playwright test that demonstrates how to generate a credential using a virtual authenticator and submit it to your FIDO server.

Prerequisites
- Node 16+ / npm
- Chrome installed (Playwright will use the chrome channel)

Install
cd playwright-tests
npm install

Run
# set SERVER_URL and endpoints as needed
SERVER_URL=https://staging.example.com:443 \
REGISTER_OPTIONS_PATH=/webauthn/register/options \
SUBMIT_CREDENTIAL_PATH=/webauthn/register/submit \
npm test

Notes
- The test uses Chrome DevTools WebAuthn emulation (CDP) and will only run on Chromium (the playwright project is configured to use the chrome channel).
- The server endpoints must support CORS if the test navigates to the server origin in the browser. Alternatively run Playwright against the server hosting the registration page.
