package com.goreecloud.android.core;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Objects;

/** An HTTPS origin and base path for user-selected, trusted self-hosted services. */
public final class TrustedEndpoint {
    private final URI base;
    private final String pathPrefix;

    private TrustedEndpoint(URI base, String pathPrefix) {
        this.base = base;
        this.pathPrefix = pathPrefix;
    }

    /** Requires HTTPS, a DNS host, a normalized path, and no userinfo, query or fragment. */
    public static TrustedEndpoint parse(String input) {
        Objects.requireNonNull(input, "input");
        final URI uri;
        try {
            uri = new URI(input);
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("Invalid endpoint URI", exception);
        }
        validate(uri, false);
        if (uri.getRawQuery() != null || uri.getRawFragment() != null) {
            throw new IllegalArgumentException("Base endpoint must not include query or fragment");
        }
        String path = uri.getRawPath();
        if (path == null || path.isEmpty()) path = "/";
        if (!path.endsWith("/")) path += "/";
        // The parsed ASCII URI already contains escaped path segments. Rebuilding it with
        // URI's component constructor would escape '%' again (e.g. %20 -> %2520).
        String ascii = uri.toASCIIString();
        URI directory = URI.create(ascii + (ascii.endsWith("/") ? "" : "/"));
        return new TrustedEndpoint(directory, path);
    }

    /** Allows resources at or below the base directory, never sibling paths or other origins. */
    public boolean contains(URI candidate) {
        if (candidate == null) return false;
        try {
            validate(candidate, true);
        } catch (IllegalArgumentException invalid) {
            return false;
        }
        if (!sameHost(base.getHost(), candidate.getHost()) || base.getPort() != candidate.getPort()) {
            return false;
        }
        String raw = candidate.getRawPath();
        String canonical = raw == null || raw.isEmpty() ? "/" : raw;
        // Also permit the exact base directory without its optional trailing slash.
        return canonical.startsWith(pathPrefix)
                || (pathPrefix.length() > 1 && canonical.equals(pathPrefix.substring(0, pathPrefix.length() - 1)));
    }

    /** Normalized HTTPS base, always ending with a slash. */
    public URI uri() { return base; }

    private static boolean sameHost(String a, String b) {
        return a != null && b != null && a.toLowerCase(Locale.ROOT).equals(b.toLowerCase(Locale.ROOT));
    }

    private static void validate(URI uri, boolean allowQuery) {
        if (!uri.isAbsolute() || uri.isOpaque() || !"https".equalsIgnoreCase(uri.getScheme())
                || uri.getHost() == null || uri.getHost().isBlank()
                || uri.getRawUserInfo() != null || uri.getRawFragment() != null
                || (!allowQuery && uri.getRawQuery() != null)) {
            throw new IllegalArgumentException("Expected a credential-free HTTPS endpoint");
        }
        if (uri.getPort() < -1 || uri.getPort() > 65535 || uri.getPort() == 0) {
            throw new IllegalArgumentException("Invalid HTTPS port");
        }
        String raw = uri.getRawPath() == null ? "/" : uri.getRawPath();
        if (!raw.startsWith("/") || raw.indexOf('\\') >= 0 || !raw.equals(uri.normalize().getRawPath())) {
            throw new IllegalArgumentException("Path must be normalized");
        }
        for (String segment : raw.split("/")) {
            if (segment.equals(".") || segment.equals("..")) {
                throw new IllegalArgumentException("Dot path segments are forbidden");
            }
        }
        // Avoid alternate encodings of separators, dot segments and percent escapes.
        String low = raw.toLowerCase(Locale.ROOT);
        if (low.contains("%2e") || low.contains("%2f") || low.contains("%5c") || low.contains("%25")) {
            throw new IllegalArgumentException("Ambiguous encoded path");
        }
    }
}
