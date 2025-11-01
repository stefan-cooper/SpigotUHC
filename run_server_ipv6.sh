#!/bin/bash

. setup_server.sh

cd server && java -Djava.net.preferIPV4stack=false -Djava.net.preferIPv6Addresses=true -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar paper-latest.jar -nogui
