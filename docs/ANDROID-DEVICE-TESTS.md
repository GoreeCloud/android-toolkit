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

The Android device compatibility GitHub Actions workflow runs the API 26 and API 35 matrix for affected changes and allows manual dispatch. A workflow committed to source is not proof that either emulator test has passed.

## Acceptance boundaries

Emulator testing does not verify physical devices, OEM-specific storage behavior, release signing, application integration, or a Stable SDK. Do not close the native device and consumer acceptance gates solely from these checks.
