#!/bin/bash

. setup_server.sh

cd server && java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar spigot-"${MINECRAFT_VERSION}".jar -nogui
