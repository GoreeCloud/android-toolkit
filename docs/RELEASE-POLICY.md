# Android Toolkit release and compatibility policy

As GoreeCloud founder, I require the Android Toolkit to remain a shared native-development foundation with explicit release evidence, protected consumer compatibility, and no misleading SDK publication claims.

**Current maturity:** experimental source, `0.1.0-dev` module version. No published Maven coordinate or Stable release is verified. This document establishes acceptance requirements, not a release announcement.

## Package ownership

- `sdk-core`: dependency-free Java 17 APIs under `com.goreecloud.android.core`.
- `android-kit`: Android library APIs under `com.goreecloud.android.kit`, minimum Android API 26, and public project dependency on `sdk-core`.
- `consumer-smoke`: a compile-only internal fixture; its APK is not an application deliverable.
- Platform design and interface tokens remain exclusively under [GC UI](https://github.com/GoreeCloud/gc-ui). Per-app data, credentials, permissions, product behavior, signing and runtime releases remain with their respective applications.

## Compatibility and versioning

1. While `0.x`, APIs may change only with documented migration impact, regression tests, and explicit review of known consumers; a module build alone is not consumer compatibility proof.
2. A future `1.0.0` requires an intentionally selected public API surface, published changelog, dependency metadata, baseline ABI review, source/binary compatibility evaluation, and reproducible dependency resolution.
3. Changes in security boundaries (URL scoping, filename restrictions, plaintext storage, retry semantics) are always potentially breaking, even when the method signatures stay unchanged.
4. Record tested Android API levels, Gradle, Android Gradle Plugin, Java, and transitive libraries for each release candidate. Do not infer compatibility with untested OEM or OS versions.
5. Dependents must pin a verified Git revision or approved release artifact. Do not invent Maven Central or GitHub Packages coordinates.

## Evidence required before publication

- CI: Python tooling suite, tracked-file security audit, JVM core checks, Android AAR build and internal consumer compile checks all succeed on the candidate commit.
- Native runtime: instrumentation succeeds on API 26 and API 35. Additional Android versions and physical devices are independent acceptance tasks.
- Packaging: inspect built AAR/JAR metadata, verify transitive dependency resolution from an independently configured consumer, validate artifact hashes, and document source provenance/licensing.
- Security: maintainer-led review of sensitive APIs, negative tests, dependency/license review, no signing keys or secrets in public Git history, safe publication permissions, and a recovery/rollback procedure.
- Consumer pilot: one real GoreeCloud Android app integrates a specific reviewed helper, retains its own security policy, passes its targeted tests, and documents a rollback.
- Release: an authorized tag and distribution destination must be decided, generated files checked against the intended source commit, and any checksums/attestations verified.
- **Do not publish an SDK as Stable while physical-device and consumer acceptance remain outstanding.**

## APK distinction

An AAR and a Java library are reusable development packages, not installable GoreeCloud application APKs. A fixture APK built solely to validate compilation is not a user-facing app. When a real GoreeCloud Android APK is delivered for testing or installation, follow the governing standalone `.apk` delivery requirement for the existing `GoreeCloud/Artifacts` Google Drive folder after applicable integrity, signing and target-device checks.

## Tracking

- [Native-device and publication acceptance](https://github.com/GoreeCloud/android-toolkit/issues/1)
- [GoreeCloud Drive pilot](https://github.com/GoreeCloud/android-toolkit/issues/2)

Until those gates are verified, maintain the project as an early working development foundation rather than a production-ready or Stable SDK.
