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

## Verified physical-device run — October 30, 2026

On a connected OnePlus N200 (DE2117) running Android 16 / API 36, Gradle's `:android-kit:connectedDebugAndroidTest` returned exit status 0. The generated JUnit XML reported **4 tests, 0 failures, 0 errors, 0 skipped**. The four test cases exercised first write/atomic replacement, safe rejection of oversized replacement, invalid filenames and limits, and package-scoped native Settings Intent construction. No toolkit test packages remained installed when checked afterward.

The checkout was source commit [cb40fb3](https://github.com/GoreeCloud/android-toolkit/commit/cb40fb355ff6258a123837e9aace08486522a5dc). A **local, uncommitted build-only substitution** changed the `sdk-core` Java toolchain selection from Java 17 to Java 21 because the authorized laptop's Java 17 runtime lacked the Java compiler. The core Java compile still set `options.release = 17`, preserving Java 17 bytecode output; Android source and instrumentation test code were not modified. The local build used Gradle 9.7.1 and JDK 21. This demonstrates a representative physical-device test, **not** an exact reproduction of the Gradle 9.6.0 / JDK 17 CI environment.

No personal files, credentials or external storage were accessed by the test cases. The test only wrote and removed synthetic files in its own app-private directory. Device identity and ADB serial are intentionally not recorded in this public repository.

## Acceptance boundaries

The API 26/35 emulator matrix and the single API 36 physical-device run do not verify all OEM/storage variants, release signing, application integration, or a Stable SDK. Additional representative devices, security and accessibility checks, and a consuming-app pilot remain outstanding. Do not close the native device and consumer acceptance gates solely from these checks.
