package com.goreecloud.android.core;

import java.net.URI;
import java.nio.charset.StandardCharsets;

/** Dependency-free Java core checks suitable for quick local and CI verification. */
public final class CoreSelfTest {
    private static int checks;

    public static void main(String[] args) {
        TrustedEndpoint endpoint = TrustedEndpoint.parse("https://cloud.example.com/dav/spaces/space-1");
        equal("https://cloud.example.com/dav/spaces/space-1/", endpoint.uri().toString());
        TrustedEndpoint encoded = TrustedEndpoint.parse("https://cloud.example.com/my%20space");
        equal("https://cloud.example.com/my%20space/", encoded.uri().toString());
        yes(encoded.contains(URI.create("https://cloud.example.com/my%20space/file.txt")));
        yes(endpoint.contains(URI.create("https://cloud.example.com/dav/spaces/space-1/folder?q=a")));
        yes(endpoint.contains(URI.create("https://cloud.example.com/dav/spaces/space-1")));
        no(endpoint.contains(URI.create("https://cloud.example.com/dav/spaces/space-10/file")));
        no(endpoint.contains(URI.create("http://cloud.example.com/dav/spaces/space-1/folder")));
        no(endpoint.contains(URI.create("https://evil.example.com/dav/spaces/space-1/folder")));
        no(endpoint.contains(URI.create("https://cloud.example.com/dav/spaces/space-1/%2e%2e/private")));
        no(endpoint.contains(URI.create("https://cloud.example.com/dav/spaces/space-1/../private")));
        no(endpoint.contains(URI.create("https://cloud.example.com/dav/spaces/space-1/folder#frag")));
        no(endpoint.contains(URI.create("https://user:pass@cloud.example.com/dav/spaces/space-1/folder")));
        fail(() -> TrustedEndpoint.parse("http://cloud.example.com"));
        fail(() -> TrustedEndpoint.parse("https://user:token@cloud.example.com"));
        fail(() -> TrustedEndpoint.parse("https://cloud.example.com/path?auth=oops"));
        fail(() -> TrustedEndpoint.parse("https://cloud.example.com/../secret"));
        fail(() -> TrustedEndpoint.parse("https://cloud.example.com/%2fprivate"));

        RetryBackoff retry = new RetryBackoff(250, 5000);
        equal(250L, retry.delayMillis(0));
        equal(2000L, retry.delayMillis(3));
        equal(5000L, retry.delayMillis(10));
        equal(5000L, retry.delayMillis(Integer.MAX_VALUE));
        equal(Long.MAX_VALUE, new RetryBackoff(Long.MAX_VALUE - 1, Long.MAX_VALUE).delayMillis(15));
        fail(() -> new RetryBackoff(0, 12));
        fail(() -> retry.delayMillis(-1));
        equal("file_01.json", SafeFileName.require("file_01.json"));
        fail(() -> SafeFileName.require("../secrets"));
        fail(() -> SafeFileName.require("a/b"));
        fail(() -> SafeFileName.require("\\windows"));
        fail(() -> SafeFileName.require(".hidden"));
        fail(() -> SafeFileName.require("a".repeat(129)));
        equal("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                Sha256.hex("abc".getBytes(StandardCharsets.UTF_8)));
        System.out.println("CoreSelfTest: " + checks + " checks passed");
    }

    private static void yes(boolean condition) { checks++; if (!condition) throw new AssertionError("Expected true"); }
    private static void no(boolean condition) { yes(!condition); }
    private static void equal(Object expected, Object actual) {
        checks++;
        if (!expected.equals(actual)) throw new AssertionError("Expected " + expected + ", got " + actual);
    }
    private static void fail(Runnable operation) {
        checks++;
        try { operation.run(); } catch (IllegalArgumentException accepted) { return; }
        throw new AssertionError("Expected IllegalArgumentException");
    }
}
