# Prioritized development roadmap

I intend the shared toolkit to reduce maintenance cost across my native Android applications without turning it into a monolith or duplicating app-specific functionality.

## Next acceptance gates

1. Pass a full AGP 9.4.0 / Gradle 9.6.0 Android library build and confirm CI jobs on the public repository; repair any configuration or API issues.
2. Add instrumentation tests on Android 8/API 26 through a modern emulator for atomic file replacement, settings Intents and API compatibility; assess physical-device variants.
3. Validate one deliberately small consumer integration (recommended: a self-contained helper in `GoreeCloud/drive`), with regression tests and no app ownership transfer.
4. Establish an approved artifact publishing policy, dependency coordinates, SBOM/provenance checks and semver-compatible API lifecycle. No Maven release should be implied before these gates pass.
5. If repeated demand is verified across two or more apps, add reusable HTTP client policies, file-provider patterns, accessibility QA fixtures, Gradle convention plugins, baseline/profile tooling, and GC UI Android adaptation **owned by GC UI**, not a competing visual authority.

## Deferred by design

Do not build generic permissions services, implicit background uploaders, new account/secret storage, telemetry pipelines, or a brand-new Android framework without independent need and proper acceptance testing.
