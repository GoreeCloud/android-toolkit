# GoreeCloud Android Toolkit

**GoreeCloud Android Toolkit** is the shared development foundation for my native GoreeCloud Android products. This public repository holds reusable Android/JVM libraries, Android-specific platform integrations, developer quality tools, and repeatable build checks. It is **not** an Android app, a replacement for Android's official SDK, or a substitute for [GoreeCloud UI](https://github.com/GoreeCloud/gc-ui), the ecosystem's experience authority.

**Stage:** early working source foundation. The [expanded SDK validation run](https://github.com/GoreeCloud/android-toolkit/actions/runs/40802885608) passed Python tooling tests, Java core checks, the Android AAR build, instrumented-test APK compilation, and a non-shipping Android consumer fixture build. API 26 and API 35 emulator tests passed in [run 40802885548](https://github.com/GoreeCloud/android-toolkit/actions/runs/40802885548); real consumer-app integration, additional physical-device/OEM validation, and release acceptance remain outstanding. The [single-device Android 16 test](docs/ANDROID-DEVICE-TESTS.md) passed with a documented local JDK toolchain workaround. No Maven publication, signed distribution APK, or production runtime validation is asserted.

## What is included

| Component | Purpose | Status |
| --- | --- | --- |
| `sdk-core` | Dependency-free, Java 17 shared library for safe HTTPS endpoint scoping, bounded retry delays, path-safe single file names, and SHA-256 integrity helpers | Implemented; core self-tests |
| `android-kit` | Native Android AAR source: app details settings Intent and bounded, app-private atomic file writes | Implemented source; Android AAR, API 26/35 emulator and single API 36 handset tests passed; further devices pending |
| `examples/standalone-consumer` | Separate Gradle root resolving Android/JVM public APIs using local composite substitution | [CI 41138181732](https://github.com/GoreeCloud/android-toolkit/actions/runs/41138181732) passed; not a real production-app adoption |
| `consumer-smoke` | Non-shipping Android compile fixture proving `android-kit` and transitive `sdk-core` APIs can be referenced from an app module | Source committed; compilation verified by successful CI run 40803172919 |
| `.github/workflows/android-device-tests.yml` | Two-emulator (API 26 and 35) instrumented storage and Settings Intent checks | Suite committed; API 26 and 35 emulator checks passed in run 40802885548 |
| `tools/apk_inspect.py` | Offline APK ZIP structure, SHA-256 and dangerous-entry checks; clearly not signature/package/device verification | Implemented; synthetic tests |
| `tools/check_binaries.py` | Offline JAR/AAR public-class, ZIP integrity and SHA-256 report in CI | Verified by [CI 41137275815](https://github.com/GoreeCloud/android-toolkit/actions/runs/41137275815); does not prove ABI compatibility |
| `tools/audit_tracked.py` | Git-tracked public-repository artifact and private-key-header checks | Implemented; offline tests |
| `.github/workflows/verify.yml` | Python tests, tracked-file scan, JDK 17 core tests, AGP 9.4 Android AAR build | Source-level and AAR checks passed in CI; API 26/35 emulators and one API 36 handset tested; additional hardware acceptance pending |

## Start developing

Requirements for Android build: **JDK 17, Gradle 9.6.0, Android SDK platform 36 and Build Tools 36.0.0**. The repository currently uses CI-installed Gradle rather than claiming an unverified wrapper binary. Android Gradle Plugin 9.4.0 provides built-in Kotlin support, so `android-kit` does not apply the old `kotlin-android` plugin.

```sh
# No Android SDK required for these local source checks:
python3 -m unittest discover -s tests -v
python3 tools/audit_tracked.py

# Android SDK and pinned Gradle installation required:
gradle :sdk-core:verifyCore :android-kit:assembleDebugAndroidTest :consumer-smoke:assembleDebug

# Inspect a test APK without uploading or installing it:
python3 tools/apk_inspect.py /absolute/path/to/app-debug.apk
```

For `sdk-core`, the dependency-free Java self-test can also be run without Gradle using `javac` and `java` (see `docs/DEVELOPMENT.md`). Consumers of the SDK currently use a Gradle project dependency or an explicitly approved local artifact; **the modules are not yet published to Maven Central or GitHub Packages**.

## Ownership and boundaries

- **Toolkit** owns common Android engineering infrastructure only. The `consumer-smoke` Android app module is a compile-time fixture, not a distributed GoreeCloud application. Each app (`launcher`, `keyboard`, `drive`, etc.) retains its source, product logic, application identity, releases and runtime acceptance.
- **GC UI** owns shared design requirements and visual tokens. This repo does not create a competing design system.
- **Android SDK / Jetpack** are external upstream platform components. This repo neither forks nor rebrands their implementations.
- **Security:** opt in to filesystem writes, never store secrets with the plaintext writer, and do not commit credentials, certificates, local SDK paths or signing materials. The static audit is defense in depth, not a guarantee that all secrets are absent.
- **APK delivery:** when an Android test/release APK is actually delivered, the standalone APK belongs in the authorized GoreeCloud Google Drive `GoreeCloud/Artifacts` folder after provenance, signing and compatibility checks. This toolkit itself does not produce a standalone app APK.

## Reference documentation

[Development](docs/DEVELOPMENT.md) · [Release policy](docs/RELEASE-POLICY.md) · [Android device tests](docs/ANDROID-DEVICE-TESTS.md) · [Consumer fixture](docs/CONSUMER-SMOKE.md) · [Architecture](docs/ARCHITECTURE.md) · [Security](docs/SECURITY.md) · [Integration](docs/INTEGRATION.md) · [Roadmap](docs/ROADMAP.md)

## License

Original source in this repository is made available under [Apache-2.0](LICENSE). Third-party SDKs, tools, and dependencies retain their own separate licensing and attribution obligations.
