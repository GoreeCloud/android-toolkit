# Independently configured Android consumer fixture

This directory contains a **separate Gradle build root**. It exists only to check that a third-party-style Android consumer can compile against a reviewed local source checkout of GoreeCloud Android Toolkit without adding its app module to the toolkit root.

The `settings.gradle.kts` file uses an explicit Gradle composite `includeBuild("../..")` and maps a declared `com.goreecloud.android:android-kit` coordinate to the source project's `:android-kit` module. No SDK is fetched from or published to Maven. The sample's only dependency on the toolkit is `implementation("com.goreecloud.android:android-kit:0.1.0-dev")`; it references public classes in `android-kit` and transitively in `sdk-core`.

With JDK 17, Gradle 9.6.0, and Android SDK 36:

```sh
gradle -p examples/standalone-consumer :app:assembleDebug --no-daemon
```

The application fixture has **no launcher activity**, **no Android permissions**, no credential storage, no networking or production behavior. The assembled APK exists for compiler and dependency-resolution checks only. **Do not deliver or install this fixture as a GoreeCloud application.**

A successful build verifies local composite dependency resolution, compile-time visibility, and module API packaging at that revision. It does **not** verify a real external GoreeCloud app, binary release compatibility, Android runtime behavior, or SDK distribution. See [integration](../../docs/INTEGRATION.md) and [release policy](../../docs/RELEASE-POLICY.md).
