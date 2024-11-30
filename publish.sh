#!/bin/bash

if [ -z "$DISCORD_WEBHOOK" ]; then
  echo "no discord webhook env var set"
  exit 1
fi

./build_plugin.sh

PLUGIN_VERSION=$(mvn help:evaluate -Dexpression=UHCPlugin.version -q -DforceStdout)
PLUGIN_LOCATION="./build/SpigotUHC-${PLUGIN_VERSION}.jar"

curl \
  -H "Content-Type: multipart/form-data" \
  -X POST \
  -F "payload_json={ \"content\":\"SpigotUHC ${PLUGIN_VERSION} is now available\" }" \
  -F "file1=@${PLUGIN_LOCATION}" \
  "${DISCORD_WEBHOOK}"

