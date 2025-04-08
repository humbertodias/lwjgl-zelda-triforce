OS := $(shell uname -s)
ARCH := $(shell uname -m)

ifeq ($(OS),Darwin)
	ifeq ($(ARCH),arm64)
		PLATFORM := macos-arm64
	else
		PLATFORM := macos
	endif
else ifeq ($(OS),Linux)
	ifeq ($(ARCH),aarch64)
		PLATFORM := linux-arm64
	else
		PLATFORM := linux
	endif
else ifeq ($(OS),Windows_NT)
	ifeq ($(ARCH),ARM64)
		PLATFORM := windows-arm64
	else
		PLATFORM := windows
	endif
endif

JAVA_OPTS=
ifeq ($(OS),Darwin)
JAVA_OPTS+=-XstartOnFirstThread
endif

package:
	echo "PLATFORM: $(PLATFORM)"
	mvn -Dlwjgl.natives=natives-$(PLATFORM) package

run:
	java $(JAVA_OPTS) -jar target/lwjgl-zelda-triforce-1.0-SNAPSHOT.jar

PLATFORMS := windows windows-arm64 linux linux-arm64 macos macos-arm64
natives:
	@for PLATFORM in $(PLATFORMS); do \
		echo "Packaging for $$PLATFORM"; \
		mvn -Dlwjgl.natives=natives-$$PLATFORM clean package ;\
		cp target/lwjgl-zelda-*.jar triforce-lwjgl-$$PLATFORM.jar ;\
	done

clean:
	mvn clean