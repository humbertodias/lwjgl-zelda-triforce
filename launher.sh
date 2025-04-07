#!/bin/bash

cd "$(dirname "$0")"

TAG_NAME=$(curl -s https://api.github.com/repos/humbertodias/lwjgl-zelda-triforce/tags | jq -r '.[0].name')
echo "Version: $TAG_NAME"

if [ ! -d "natives" ]; then
    echo "Downloading natives.zip"
    curl -L https://github.com/humbertodias/lwjgl-zelda-triforce/releases/download/$TAG_NAME/natives.zip -o natives.zip
    unzip natives.zip && rm natives.zip
fi

JAVA_OPTS="-Djava.library.path=natives"
OS="$(uname -s)"
if [ "$OS" = "Darwin" ]; then
  JAVA_OPTS="$JAVA_OPTS -XstartOnFirstThread"
fi
echo "JAVA_OPTS=$JAVA_OPTS"

java "$JAVA_OPTS" -jar triforce-lwjgl.jar