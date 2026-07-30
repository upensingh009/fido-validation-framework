#!/usr/bin/env bash
# Run the JMeter test plan with a single HOST:PORT variable and scheme
# If fixture files exist in jmeter-tests/fixtures and -J overrides for payloads are not provided,
# the script will automatically load the fixtures and pass them to JMeter as -JREGISTRATION_PAYLOAD etc.
# Usage: ./run_jmeter.sh <scheme> <host:port> [additional -J overrides]
# Example: ./run_jmeter.sh https staging.example.com:443 -JREGISTER_OPTIONS_PATH=/api/webauthn/register/options

set -euo pipefail
if [ "$#" -lt 2 ]; then
  echo "Usage: $0 <scheme> <host:port> [jmeter -J overrides]"
  exit 1
fi

SCHEME=$1
HOSTPORT=$2
shift 2

# Split host:port
HOST=${HOSTPORT%%:*}
PORT=${HOSTPORT#*:}
if [ "$HOST" = "$PORT" ]; then
  # no port provided
  PORT=80
fi

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

JMETER_CMD=(jmeter -n -t "$SCRIPT_DIR/fido-validation-testplan.jmx")
JMETER_CMD+=( -JSERVER_SCHEME=${SCHEME} -JSERVER_HOST=${HOST} -JSERVER_PORT=${PORT} )

# append any additional -J overrides provided by user
USER_OVERRIDES=()
if [ "$#" -gt 0 ]; then
  USER_OVERRIDES=("$@")
  JMETER_CMD+=("${USER_OVERRIDES[@]}")
fi

# If fixtures exist and user did not provide explicit payload overrides, add them
# Look for registration_sample.json, assertion_sample.json, tampered_signature.json, tampered_attestation.json
if [ -f "$SCRIPT_DIR/fixtures/registration_sample.json" ] && ! printf '%s\n' "${USER_OVERRIDES[@]:-}" | grep -q -- '-JREGISTRATION_PAYLOAD='; then
  REG_PAYLOAD=$(cat "$SCRIPT_DIR/fixtures/registration_sample.json")
  JMETER_CMD+=("-JREGISTRATION_PAYLOAD=$REG_PAYLOAD")
fi

if [ -f "$SCRIPT_DIR/fixtures/assertion_sample.json" ] && ! printf '%s\n' "${USER_OVERRIDES[@]:-}" | grep -q -- '-JAUTHENTICATION_PAYLOAD='; then
  AUTH_PAYLOAD=$(cat "$SCRIPT_DIR/fixtures/assertion_sample.json")
  JMETER_CMD+=("-JAUTHENTICATION_PAYLOAD=$AUTH_PAYLOAD")
fi

if [ -f "$SCRIPT_DIR/fixtures/tampered_signature.json" ] && ! printf '%s\n' "${USER_OVERRIDES[@]:-}" | grep -q -- '-JTAMPERED_SIGNATURE_PAYLOAD='; then
  TAMP_SIG=$(cat "$SCRIPT_DIR/fixtures/tampered_signature.json")
  JMETER_CMD+=("-JTAMPERED_SIGNATURE_PAYLOAD=$TAMP_SIG")
fi

if [ -f "$SCRIPT_DIR/fixtures/tampered_attestation.json" ] && ! printf '%s\n' "${USER_OVERRIDES[@]:-}" | grep -q -- '-JTAMPERED_ATTESTATION_PAYLOAD='; then
  TAMP_ATT=$(cat "$SCRIPT_DIR/fixtures/tampered_attestation.json")
  JMETER_CMD+=("-JTAMPERED_ATTESTATION_PAYLOAD=$TAMP_ATT")
fi

# default result file
JMETER_CMD+=( -l "$(pwd)/jmeter-results.jtl" )

echo "Running: ${JMETER_CMD[*]}"
"${JMETER_CMD[@]}"
