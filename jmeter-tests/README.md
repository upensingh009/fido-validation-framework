# FIDO Validation JMeter Tests

This directory contains a parameterized JMeter test plan that implements thin HTTP-level checks for a FIDO/WebAuthn server. The plan is intentionally parameterized so you only need to provide the server host/port/scheme and (optionally) payloads.

Files
- fido-validation-testplan.jmx — the JMeter test plan (one TestPlan with multiple ThreadGroups). Edit in JMeter GUI or run from CLI.

Variables (User Defined Variables in the TestPlan)
- SERVER_SCHEME (default https)
- SERVER_HOST (default localhost)
- SERVER_PORT (default 8443)
- REGISTER_OPTIONS_PATH (default /webauthn/register/options)
- SUBMIT_CREDENTIAL_PATH (default /webauthn/register/submit)
- AUTH_OPTIONS_PATH (default /webauthn/auth/options)
- SUBMIT_ASSERTION_PATH (default /webauthn/auth/submit)
- REGISTRATION_PAYLOAD — placeholder JSON body for credential submission
- AUTHENTICATION_PAYLOAD — placeholder JSON body for assertion submission
- TAMPERED_SIGNATURE_PAYLOAD — placeholder JSON body for tampered signature
- TAMPERED_ATTESTATION_PAYLOAD — placeholder JSON body for tampered attestation

How to run (CLI)
1. Install JMeter (>= 5.4 recommended).
2. Run from the command line and pass overrides as -J properties, for example:

jmeter -n -t jmeter-tests/fido-validation-testplan.jmx \
  -JSERVER_SCHEME=https -JSERVER_HOST=staging.example.com -JSERVER_PORT=443 \
  -JREGISTER_OPTIONS_PATH=/api/webauthn/register/options \
  -JSUBMIT_CREDENTIAL_PATH=/api/webauthn/register/submit \
  -JAUTH_OPTIONS_PATH=/api/webauthn/auth/options \
  -JSUBMIT_ASSERTION_PATH=/api/webauthn/auth/submit \
  -JREGISTRATION_PAYLOAD='{...}' -JAUTHENTICATION_PAYLOAD='{...}' \
  -l results.jtl

3. Open results.jtl with the JMeter GUI (File -> Open) or convert to HTML report.

Notes
- These JMeter tests are "thin" HTTP checks. They cannot perform full WebAuthn cryptographic flows (create/attest assertions) within JMeter.
- For end-to-end credential creation and assertion generation, use the Playwright-based test harness which can run a virtual authenticator inside the browser and then POST the generated artifacts to the validator API.
- You can duplicate and adapt the provided ThreadGroups in the JMeter GUI to add additional validation steps (e.g., tampering payloads, challenge replay scenarios).

If you want, I can:
- expand the JMX with additional ThreadGroups for each remaining test case (tampered payloads, replay checks, multi-auth runs), including sample payload templates; or
- provide a Playwright suite that will perform full create/assert flows and then call the validator API for deep validation.

Tell me which further step you prefer and provide any endpoint path differences if needed (or I can continue and create more complete JMX ThreadGroups programmatically).