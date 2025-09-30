#!/bin/sh

set -ex

# if you are not seeing this version of mvn installed, set REFRESH_BUILD env var to true
if [ -z "$MAVEN_HOME" ]; then
  export MAVEN_HOME=./server/apache-maven-3.9.6
  export PATH="${PATH}":${MAVEN_HOME}/bin
fi

# if you are seeing mvn not found, set this
if [ "$(uname)" != "Darwin" ]; then
  export JAVA_HOME=/c/Program\ Files/Java/jdk-21
fi

REFRESH_BUILD=${REFRESH_BUILD:-false}
MINECRAFT_VERSION=${MINECRAFT_VERSION:-1.21.9}
PAPER_BUILD=${PAPER_BUILD:-37}
export MINECRAFT_VERSION="${MINECRAFT_VERSION}"

./build_plugin.sh

mkdir -p server
mkdir -p server/plugins
PLUGIN_VERSION=$(mvn help:evaluate -Dexpression=UHCPlugin.version -q -DforceStdout)
find ./server/plugins/ -name 'SpigotUHC-*.jar' -delete
cp build/SpigotUHC-"${PLUGIN_VERSION}".jar server/plugins/SpigotUHC-"${PLUGIN_VERSION}".jar

if [ "${REFRESH_BUILD}" = "true" ]; then
  cd server
  curl -o paper-latest.jar "https://api.papermc.io/v2/projects/paper/versions/${MINECRAFT_VERSION}/builds/${PAPER_BUILD}/downloads/paper-${MINECRAFT_VERSION}-${PAPER_BUILD}.jar"
  cd ..
fi

