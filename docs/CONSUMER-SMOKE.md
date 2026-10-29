# Internal Android consumer smoke test

As GoreeCloud founder, I require the shared toolkit to be consumable by real Android Gradle modules without inventing a public SDK release.

The `consumer-smoke` module is an internal, non-shipping Gradle Android application fixture. It uses `implementation(project(":android-kit"))` only; its Java source references public Android Kotlin helper APIs and `sdk-core` symbols transitively exposed by `android-kit`. It does not contain a launcher activity, user data, network endpoints to contact, or UI functionality.

## Build-time acceptance

Use JDK 17, AGP 9.4, Gradle 9.6, and Android SDK 36:

`gradle :sdk-core:verifyCore :android-kit:assembleDebugAndroidTest :consumer-smoke:assembleDebug --no-daemon`

A passing consumer fixture build demonstrates source-level Gradle dependency resolution, API visibility, compilation, and APK assembly. It does not demonstrate application startup, on-device execution, cross-repository adoption, Google Play readiness, or physical-device compatibility. The fixture APK is not distributed.

## Controlled adoption

A real GoreeCloud application may adopt a reviewed toolkit revision only after compatibility, security, app-specific tests and a rollback path have been verified. See [Integration](INTEGRATION.md), [Android device tests](ANDROID-DEVICE-TESTS.md), and [Roadmap](ROADMAP.md).

The GC UI design authority remains [GoreeCloud/gc-ui](https://github.com/GoreeCloud/gc-ui).
