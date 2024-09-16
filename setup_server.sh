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
MINECRAFT_VERSION=${MINECRAFT_VERSION:-1.21.1}
export MINECRAFT_VERSION=${MINECRAFT_VERSION}

./build_plugin.sh

mkdir -p server
mkdir -p server/plugins
cp build/SpigotUHC-0.0.1.jar server/plugins/SpigotUHC-0.0.1.jar

if [ "${REFRESH_BUILD}" = "true" ]; then
  cd server
  curl -o BuildTools.jar https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
  java -jar BuildTools.jar --rev "${MINECRAFT_VERSION}"
  cd ..
fi

