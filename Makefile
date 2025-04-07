.PHONY: natives

OS := $(shell uname -s)

JAVA_OPTS = -Djava.library.path=natives
ifeq ($(OS),Darwin)
JAVA_OPTS += -XstartOnFirstThread
endif

package:
	mvn package

run:
	java ${JAVA_OPTS} -jar target/lwjgl-zelda-triforce-1.0-SNAPSHOT.jar

LWJGL_VERSION=3.3.6
natives:
	mkdir -p natives
	find ${HOME}/.m2 -name lwjgl-*-${LWJGL_VERSION}-natives-*.jar -exec unzip -o {} -d natives \;

clean:
	rm -rf natives target