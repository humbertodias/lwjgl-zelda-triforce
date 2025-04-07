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

NATIVES_CLASSIFIER := windows windows-arm64 linux linux-arm64 macos macos-arm64
natives-classifier:
	@for CLASSIFIER in $(NATIVES_CLASSIFIER); do \
		echo "Downloading classifier dependencies: $$CLASSIFIER"; \
		mvn dependency:go-offline -Dlwjgl.natives=natives-$$CLASSIFIER || exit $$?; \
	done

LWJGL_VERSION=3.3.6
natives:	natives-classifier
	mkdir -p natives
	find ${HOME}/.m2 -name lwjgl-*-${LWJGL_VERSION}-natives-*.jar -exec unzip -o {} -d natives \;

clean:
	rm -rf natives target