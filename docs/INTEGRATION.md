# Consuming the Android Toolkit

A GoreeCloud Android application can add these modules as a local Gradle composite or project dependency after evaluating compatibility with its own build and security requirements.

## Example local composite development

When app and toolkit are sibling projects, use a controlled include-build setup **only when its build graph and module coordinates are verified**. A simple temporary multi-project checkout can instead add `include(":sdk-core", ":android-kit")` in a shared settings file with accurate project directory mapping and `implementation(project(":android-kit"))` in the application module.

This repository does not currently publish a binary or stable Maven coordinate. **Do not add `com.goreecloud.android:*` as an unverified public Maven dependency.**

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
