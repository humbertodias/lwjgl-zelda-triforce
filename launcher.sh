#!/bin/bash

cd "${BASH_SOURCE%/*}" || exit

JAVA_OPTS="-Djava.library.path=natives"
[ "$(uname -s)" = "Darwin" ] && JAVA_OPTS+=" -XstartOnFirstThread"

exec java $JAVA_OPTS -jar triforce-lwjgl.jar