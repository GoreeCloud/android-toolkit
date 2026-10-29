# Architecture and responsibility boundaries

My GoreeCloud Android Toolkit is a reusable engineering library and tooling repository, not a standalone operating-system application.

```text
Android products (launcher, keyboard, drive, future apps)
  ├─ Own app UX and GC UI adaptation
  ├─ Own credentials, platform permissions, product state and releases
  ├─ Optional project dependency → android-kit (Android native library)
  │                                └─ sdk-core (pure JVM policies)
  └─ Optional direct dependency → sdk-core

Development / CI
  ├─ tools/audit_tracked.py   public source safety preflight
  ├─ tools/apk_inspect.py    bounded offline structural APK inspection
  └─ workflow                Python tests + core self-tests + AAR build
```

## Stable boundaries to develop

- `sdk-core`: cross-Android Java/Kotlin callable primitives with no Android runtime, network access, persistence or dependency on UI frameworks.
- `android-kit`: deliberately small native Android helpers built on system APIs; depends on `sdk-core` and has no permissions of its own.
- `tools`: standalone Python 3 utilities; do not require application credentials or telemetry.

## Explicit non-goals

No replacement Android platform SDK, duplicate GC UI token library, generic credential vault, cross-app singletons, self-hosted service implementations, bundled private APIs, forced analytics, automatic consumer migrations, or product-level ownership transfers. Optional shared network clients, test fixtures, Gradle convention plugins and version catalogs are roadmap candidates subject to verified cross-app demand.
