#!/usr/bin/env bash
# Run the JMeter test plan with a single HOST:PORT variable and scheme
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

JMETER_CMD=(jmeter -n -t "$(dirname "$0")/fido-validation-testplan.jmx")
JMETER_CMD+=( -JSERVER_SCHEME=${SCHEME} -JSERVER_HOST=${HOST} -JSERVER_PORT=${PORT} )

# append any additional -J overrides
if [ "$#" -gt 0 ]; then
  JMETER_CMD+=("$@")
fi

# default result file
JMETER_CMD+=( -l "$(pwd)/jmeter-results.jtl" )

echo "Running: ${JMETER_CMD[*]}"
"${JMETER_CMD[@]}"
