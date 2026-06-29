# AGENTS.md — WechatMultiWebview

> AI agent guide for working on this project.

## Project Overview

**微信网页多窗口** — An Xposed module that restores WeChat's `//multiwebview` feature. It hooks `Activity.startActivity` in WeChat (`com.tencent.mm`) to add `FLAG_ACTIVITY_NEW_DOCUMENT` and `FLAG_ACTIVITY_MULTIPLE_TASK` flags, so web pages open in separate tasks visible in the Android recents list.

- **Module ID**: `com.zhangnew.wechatmultiwebview`
- **Target app**: WeChat (`com.tencent.mm`)
- **Xposed API**: Modern libxposed API 102 (`io.github.libxposed:api:102.0.0`)

## Build Requirements

- **JDK 17** — AGP 8.5.0 requires Java 11+; the system default may be an older JDK which will fail.
- **Android SDK** with `compileSdk 34`
- **Gradle 8.7** (wrapper included)

```bash
# Set JAVA_HOME to JDK 17 before building
# macOS:
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
# Linux:
export JAVA_HOME=/usr/lib/jvm/java-17

./gradlew assembleRelease

# Or simply use the Makefile (auto-detects JDK 17 on macOS)
make release
```

## Project Structure

```
WechatMultiWebview/
├── build.gradle                        # Root: AGP 8.5.0, Maven Central repo
├── settings.gradle                     # Single module: :app
├── Makefile                            # Build automation (auto-detects JDK 17)
├── .env.example                        # Environment variable template
├── AGENTS.md                           # This file
├── app/
│   ├── build.gradle                    # Module config, libxposed dependency
│   ├── proguard-rules.pro              # Includes libxposed keep rules
│   └── src/main/
│       ├── AndroidManifest.xml         # Module declaration (android:description, no legacy meta-data)
│       ├── java/.../XposedMain.java    # Sole source — the hook entry class
│       ├── resources/META-INF/xposed/
│       │   ├── java_init.list          # Entry class pointer (replaces assets/xposed_init)
│       │   ├── module.prop             # API version & scope config
│       │   └── scope.list              # Target package list
│       └── res/values/
│           ├── strings.xml             # app_name, module_description
│           └── arrays.xml              # Legacy scope (no longer used by modern API)
└── com.zhangnew.wechatmultiwebview/    # Separate repo: LSPosed Modules Repo metadata
```

## Xposed API — Modern (102) vs Legacy (82)

This project was migrated from the legacy `de.robv.android.xposed:api:82` to the modern `io.github.libxposed:api:102.0.0`. Key differences:

| Aspect | Legacy (82) | Modern (102) |
|--------|-------------|--------------|
| Dependency | `de.robv.android.xposed:api:82` | `io.github.libxposed:api:102.0.0` |
| Repository | rovo89/XposedBridge gh-pages | Maven Central |
| Entry class | implements `IXposedHookLoadPackage` | extends `XposedModule` |
| Entry method | `handleLoadPackage(LoadPackageParam)` | `onPackageLoaded(PackageLoadedParam)` |
| Hook API | `XposedHelpers.findAndHookMethod()` + `XC_MethodHook` | `hook(method).intercept(chain -> {...})` |
| Init file | `assets/xposed_init` | `META-INF/xposed/java_init.list` |
| Scope | `AndroidManifest.xml` meta-data + `arrays.xml` | `META-INF/xposed/scope.list` |
| Module info | Manifest `xposedmodule`/`xposedminversion` meta-data | `META-INF/xposed/module.prop` |
| Description | Manifest `xposeddescription` meta-data | `android:description` in `<application>` |
| minSdk | 24 | 26 (required by modern API) |

### Hook Pattern (Modern API)

```java
// Inside XposedModule subclass
Method method = TargetClass.getDeclaredMethod("methodName", ArgType.class);
hook(method).intercept(chain -> {
    // before: read/modify args
    Object arg0 = chain.getArg(0);
    // call original
    Object result = chain.proceed();
    // after: modify result
    return modifiedResult;
});
```

### Module Configuration Files

**`META-INF/xposed/java_init.list`** — one fully-qualified class name per line:
```
com.zhangnew.wechatmultiwebview.XposedMain
```

**`META-INF/xposed/module.prop`** — Java properties format:
```properties
minApiVersion=102
targetApiVersion=102
staticScope=true
```

**`META-INF/xposed/scope.list`** — one package name per line:
```
com.tencent.mm
```

### ProGuard Rules

When `minifyEnabled` is set to `true`, these rules are required:

```proguard
-dontwarn io.github.libxposed.annotation.**
-adaptresourcefilecontents META-INF/xposed/java_init.list
-keep,allowoptimization,allowobfuscation public class * extends io.github.libxposed.api.XposedModule {
    public <init>();
}
```

## Key Conventions

- The Xposed API dependency is `compileOnly` — it is provided at runtime by the Xposed/LSPosed framework and must not be bundled into the APK.
- The `packaging.resources.merges += 'META-INF/xposed/*'` rule in `app/build.gradle` ensures xposed metadata files are correctly merged into the APK.
- The `com.zhangnew.wechatmultiwebview/` directory is a **separate git repository** for the LSPosed Modules Repo; do not modify it unless updating module listing metadata.
- This module has **no UI** (no Activities); it is a pure hook module.

## References

- [libxposed/api](https://github.com/libxposed/api) — Modern Xposed Module API
- [libxposed/example](https://github.com/libxposed/example) — Example module using modern API
- [Develop Xposed Modules Using Modern Xposed API](https://github.com/LSPosed/LSPosed/wiki/Develop-Xposed-Modules-Using-Modern-Xposed-API) — Migration guide
- [libxposed Javadoc](https://libxposed.github.io/api/) — API reference
