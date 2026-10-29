package com.goreecloud.android.core;

/** Bounded deterministic exponential retry intervals; call sites decide when retries are safe. */
public final class RetryBackoff {
    private final long initialMs;
    private final long maxMs;

    public RetryBackoff(long initialMs, long maxMs) {
        if (initialMs <= 0 || maxMs < initialMs) {
            throw new IllegalArgumentException("Expected 0 < initialMs <= maxMs");
        }
        this.initialMs = initialMs;
        this.maxMs = maxMs;
    }

    /** retryIndex = 0 is the first retry; caps safely without integer overflow. */
    public long delayMillis(int retryIndex) {
        if (retryIndex < 0) throw new IllegalArgumentException("retryIndex must be nonnegative");
        long delay = initialMs;
        for (int i = 0; i < retryIndex && delay < maxMs; i++) {
            if (delay > maxMs / 2) return maxMs;
            delay = Math.min(maxMs, delay * 2);
        }
        return delay;
    }
}
