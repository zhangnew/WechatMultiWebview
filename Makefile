# WechatMultiWebview Makefile
# 微信网页多窗口 - Xposed Module

# Auto-detect JDK 17 (required by AGP 8.5.0+)
# macOS: uses /usr/libexec/java_home; Linux: falls back to /usr/lib/jvm/java-17
# Override by: export JAVA_HOME=/custom/path && make release
ifeq ($(JAVA_HOME),)
  ifneq ($(shell which /usr/libexec/java_home 2>/dev/null),)
    export JAVA_HOME := $(shell /usr/libexec/java_home -v 17 2>/dev/null)
  endif
  ifeq ($(JAVA_HOME),)
    export JAVA_HOME := /usr/lib/jvm/java-17
  endif
endif

APK_DIR  := app/build/outputs/apk/release
APK_FILE := $(shell ls $(APK_DIR)/*.apk 2>/dev/null | head -1)

.PHONY: all debug release clean verify install

all: release verify

# Build signed release APK
release:
	./gradlew assembleRelease

# Build debug APK
debug:
	./gradlew assembleDebug

# Clean build artifacts
clean:
	./gradlew clean

# Verify APK signature and Xposed metadata
verify: release
	@test -n "$(APK_FILE)" || (echo "Error: No APK found in $(APK_DIR)" && exit 1)
	@echo "Verifying APK: $(APK_FILE)"
	@echo "Checking Xposed metadata..."
	@unzip -l $(APK_FILE) | grep -q "META-INF/xposed/java_init.list" && echo "  ✓ java_init.list" || (echo "  ✗ java_init.list MISSING" && exit 1)
	@unzip -l $(APK_FILE) | grep -q "META-INF/xposed/module.prop" && echo "  ✓ module.prop" || (echo "  ✗ module.prop MISSING" && exit 1)
	@unzip -l $(APK_FILE) | grep -q "META-INF/xposed/scope.list" && echo "  ✓ scope.list" || (echo "  ✗ scope.list MISSING" && exit 1)
	@echo "Checking signature..."
	@apksigner verify --verbose $(APK_FILE)
	@echo "All checks passed ✓"

# Install APK to connected device
install: release
	@test -n "$(APK_FILE)" || (echo "Error: No APK found" && exit 1)
	adb install -r $(APK_FILE)
