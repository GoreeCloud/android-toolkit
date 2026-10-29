# Security and privacy boundaries

I require shared Android libraries to minimize permissions, local data exposure, identity coupling, and nonessential network traffic.

- `TrustedEndpoint` rejects non-HTTPS base URLs, embedded credentials, fragments, query strings in bases, non-normalized path traversal and ambiguous encoded separators. This is a URL-scope helper, **not** proof of server trust, TLS pinning, protection against DNS rebinding or HTTP redirect safety. Callers must enforce their own redirect and credential-disclosure policies. It does not make an arbitrary server safe.
- `RetryBackoff` computes delay only. Consumers must decide which operations are idempotent and retryable, respect cancellation, and add jitter where appropriate to avoid a coordinated retry storm.
- `AppScopedAtomicWriter` writes bounded plaintext to Android private app files. It is **not** encrypted storage; do not use it for tokens, passwords, keys or sensitive records. Use an app-specific cryptographic design with Android Keystore where necessary.
- `Sha256.hex` computes integrity digests, not an authenticity signature.
- `apk_inspect.py` reads untrusted APK ZIP structure within configurable limits and reports that signing, package identity and device validation are NOT checked. Independently verify apksigner output and the target APK package before distribution.
- `audit_tracked.py` rejects well-known sensitive tracked filenames, symlinks, and PEM private-key headers. This static check is intentionally incomplete and cannot establish an absence of leaks. Review all public changes for personal data and secrets.

Do not commit signing keystores, credentials, user records, local Gradle caches, Android SDK paths, `local.properties`, or third-party copyrighted source without proper attribution and licensing. There is no automatic telemetry or network service in this toolkit.
