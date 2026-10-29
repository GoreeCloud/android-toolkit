# Development and verification

As GoreeCloud's founder, I maintain this repository as shared engineering infrastructure for my Android applications. A passing static test or build is not a device-runtime acceptance result.

## Supported local checks

Python 3.10+:

```sh
python3 -m unittest discover -s tests -v
python3 tools/audit_tracked.py
```

JDK 17+ (dependency-free core verification without Gradle):

```sh
mkdir -p /tmp/gc-android-classes
javac -d /tmp/gc-android-classes sdk-core/src/main/java/com/goreecloud/android/core/*.java sdk-core/src/test/java/com/goreecloud/android/core/CoreSelfTest.java
java -cp /tmp/gc-android-classes com.goreecloud.android.core.CoreSelfTest
```

The standalone test above checks the same Java implementation the Android library consumes; it does not compile Android-specific code.

## Gradle/Android library

Use JDK 17, Gradle 9.6.0, Android SDK 36, and Build Tools 36.0.0:

```sh
gradle :sdk-core:verifyCore :android-kit:assembleDebug :android-kit:assembleDebugAndroidTest :consumer-smoke:assembleDebug --no-daemon
```

AGP 9.4.0 includes built-in Kotlin; do **not** apply `org.jetbrains.kotlin.android` separately to `android-kit`. The `sdk-core` module uses the Java library plugin and can be consumed from Kotlin or Java.

## Adding shared code

- Add only stable, app-agnostic behavior. Keep application policy and product features in their owning repositories.
- Prefer dependency-free helpers and platform APIs when suitable; justify and license new dependencies.
- Add a deterministic test for each pure JVM policy. Add Android instrumentation tests when system APIs, permissions, lifecycles or real storage behavior matter.
- Do not include personal data in fixtures, logs or CI artifacts.
- Treat SDK/API-breaking changes as consumer migrations. No binary compatibility promise has yet been made.

## Android device and consumer contract tests

The [Android emulator tests](ANDROID-DEVICE-TESTS.md) exercise platform-specific storage and Settings intents at API 26 and API 35 when their GitHub Actions jobs complete successfully. The [non-shipping consumer fixture](CONSUMER-SMOKE.md) compiles `android-kit` and its transitive `sdk-core` APIs from a minimal application module. Build success is narrower than emulator or physical-device runtime acceptance; do not deliver the consumer fixture as an app APK.
