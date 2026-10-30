# Android instrumentation and acceptance

As GoreeCloud founder, I require Android-specific behavior to be checked on actual Android runtimes before claiming device-level acceptance. This is an Android library test harness, not a consumer-app release.

## Checks and environment

The AndroidX JUnit4 instrumentation suite targets Google APIs emulators on API 26 (Android 8) and API 35 (Android 15). It checks:

- App-private atomic first writes and replacement.
- Oversized replacement rejection without loss of the previously saved file.
- Unsafe filename and configuration rejection.
- Correct native app-details Settings Intent construction without launching UI.

The tests use synthetic data; no credentials, user files, special permissions or application network requests are needed. Tests remove their temporary private test files.

## Execute

With JDK 17, Gradle 9.6.0, Android SDK platform 36 and an available Android emulator:

- Build test APK: `gradle :android-kit:assembleDebugAndroidTest`
- Run on a connected emulator: `gradle :android-kit:connectedDebugAndroidTest --no-daemon`

The [Android device compatibility workflow](https://github.com/GoreeCloud/android-toolkit/actions/runs/40802885548) executed instrumented tests on API 26 and API 35 emulators successfully on October 29, 2026 UTC. Both jobs completed successfully. This is emulator runtime evidence, not physical-device acceptance.

## Acceptance boundaries

Emulator testing does not verify physical devices, OEM-specific storage behavior, release signing, application integration, or a Stable SDK. Do not close the native device and consumer acceptance gates solely from these checks.
