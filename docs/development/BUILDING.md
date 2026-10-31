# Building AIC

## Requirements

- JDK 21
- POSIX shell (Linux/macOS) or Windows Command Prompt/PowerShell
- Network access for the first Gradle distribution and dependency resolution

The repository includes the Gradle 9.3.1 Wrapper. A separately installed Gradle distribution is not required.

## Verify the inference API module

Linux / macOS:

```bash
./gradlew --version
./gradlew --no-daemon :core:inference-api:test
```

Windows:

```bat
gradlew.bat --version
gradlew.bat --no-daemon :core:inference-api:test
```

For a full module build:

```bash
./gradlew --no-daemon :core:inference-api:build
```

On Linux/macOS, the checked-in `gradlew` script must retain its executable bit.

## Wrapper integrity

- The Wrapper JAR is sourced from the official Gradle `v9.3.1` release.
- CI runs Gradle's `wrapper-validation` action before invoking the Wrapper.
- `distributionSha256Sum` pins verification of the downloaded Gradle 9.3.1 binary distribution.
- Do not update the Wrapper JAR or distribution URL independently; update them together and review the official Gradle checksums.

## Current scope

This checks the backend-neutral API contract and request/model metadata validation. It does not download a model, run inference, build an APK, or test Binder/SELinux isolation. Those belong to later Phase 1 work packages.

CI runs the same test and build tasks on pushes and pull requests targeting `main`.
