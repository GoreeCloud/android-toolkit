# Consuming the Android Toolkit

A GoreeCloud Android application can add these modules as a local Gradle composite or project dependency after evaluating compatibility with its own build and security requirements.

## Example local composite development

When app and toolkit are sibling projects, use a controlled include-build setup **only when its build graph and module coordinates are verified**. A simple temporary multi-project checkout can instead add `include(":sdk-core", ":android-kit")` in a shared settings file with accurate project directory mapping and `implementation(project(":android-kit"))` in the application module.

This repository does not currently publish a binary or stable Maven coordinate. **Do not add `com.goreecloud.android:*` as an unverified public Maven dependency.**

## Standalone composite-build consumer fixture

An [independently configured Gradle consumer](../examples/standalone-consumer/README.md) demonstrates how a separate Android application project can resolve the toolkit via `includeBuild`, without falsely assuming that modules have been published to Maven. The example declares a source-only `com.goreecloud.android:android-kit:0.1.0-dev` module coordinate that is substituted locally by its included build. The public JVM `sdk-core` API is consumed transitively.

It is a **compile fixture**, not a released Android app, an external repository adoption, or a published package. The standalone build and the toolkit internal consumer must both compile before this dependency-resolution approach can be called verified.

## Example calls

```kotlin
import com.goreecloud.android.core.TrustedEndpoint
import com.goreecloud.android.core.RetryBackoff

val endpoint = TrustedEndpoint.parse("https://cloud.example.com/dav/spaces/my-space/")
val inScope = endpoint.contains(java.net.URI("https://cloud.example.com/dav/spaces/my-space/file"))
val firstRetryMs = RetryBackoff(250, 30_000).delayMillis(0)
```

Android platform helpers, after adding `:android-kit`:

```kotlin
val intent = AndroidSettingsIntents.applicationDetails(context)
// Explicitly launch only when your UI workflow requests it.

val writer = AppScopedAtomicWriter(context)
writer.write("preferences-export.json", serializedBytes) // Never put secrets here.
```

These examples illustrate source APIs; no cross-repository consumer adoption has been verified. Consumer-specific permission, data, and lifecycle validation is required before integrating.


## How I intend to use the toolkit

I intend to use GoreeCloud Android Toolkit as my shared Android engineering foundation. Reusable, independently testable code belongs here when several native applications benefit from it. Each application remains responsible for its own features, data, permissions, build, and release.

### Planned consumers

| Application | Intended adoption | Current status |
| --- | --- | --- |
| [GoreeCloud Drive](https://github.com/GoreeCloud/drive) | First integration pilot: consider HTTPS endpoint scoping, retry delay policy, checksums, and bounded local file utilities. Preserve the existing WebDAV security rules, app-token protection, and failure handling. | Proposed; no consumer integration verified |
| [GoreeCloud Launcher](https://github.com/GoreeCloud/launcher) | Consider shared Android settings and local file helpers when reuse reduces duplication. Preserve home-role consent and workspace backup compatibility. | Conditional; no consumer integration verified |
| [GoreeCloud Keyboard](https://github.com/GoreeCloud/keyboard) | Consider developer audits, APK inspection, testing utilities and narrow platform helpers. Preserve the attributed FlorisBoard engine and privacy boundaries. | Conditional; no consumer integration verified |
| Future Android products | Adopt proven helpers after a specific need and compatibility evidence exist. | Planned |

### Controlled adoption process

1. Verify that the feature is genuinely shared rather than owned by an individual app, Android itself, or [GoreeCloud UI](https://github.com/GoreeCloud/gc-ui).
2. Select the smallest useful module: `sdk-core` for Android-independent Java utilities or `android-kit` for Android-specific behavior.
3. Integrate a reviewed revision with a verified local Gradle project dependency. The toolkit has no published stable Maven package yet.
4. Test the toolkit and consuming app separately, including security boundaries, regression behavior, native Android compatibility and relevant device tests.
5. Record the adopted revision, integration details and verification evidence in the consumer repository. Leave application releases and product behavior under each application's control.
6. Introduce versioned package distribution only after API, supply-chain, reproducibility and consumer acceptance requirements have been met.

### Reusable quality tools

I will use the toolkit's offline APK structural inspector, tracked-file safety audit, Java core tests and Android AAR build checks to support Android engineering. These checks complement, but do not replace, each application's CI and runtime tests. The APK inspector is not a signing or device-compatibility validator. The toolkit produces shared libraries, not application APKs. Delivered app APKs must follow the governing standalone Google Drive artifact process.

Later additions may include Gradle convention plugins, compatible network and file utilities, test fixtures, and accessibility QA tooling, but only after verified cross-product need. GC UI remains my exclusive design and experience authority; the toolkit will not create a competing design system.

**Acceptance:** A consuming app adopts a scoped, reviewed toolkit API; appropriate toolkit and app tests pass; security and compatibility are preserved; and the integration is documented with verifiable evidence. None of the consumer integrations in this section is currently represented as complete.
