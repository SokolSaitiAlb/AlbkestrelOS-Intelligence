# Building AIC

## Requirements

- JDK 21
- Gradle 9.3.1 or a compatible Gradle 9.x release
- Network access for the first Gradle dependency resolution

The initial module is Kotlin/JVM and does not yet require Android SDK or NDK.

## Verify the inference API module

```bash
gradle --version
gradle --no-daemon :core:inference-api:test
```

For a full module build:

```bash
gradle --no-daemon :core:inference-api:build
```

## Current scope

This checks the backend-neutral API contract and request/model metadata validation. It does not download a model, run inference, build an APK, or test Binder/SELinux isolation. Those belong to later Phase 1 work packages.

CI runs the same test task on pushes and pull requests.
