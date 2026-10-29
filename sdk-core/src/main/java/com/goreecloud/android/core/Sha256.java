package com.goreecloud.android.core;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/** SHA-256 integrity hash. Not a signing, authenticity, or encryption primitive. */
public final class Sha256 {
    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private Sha256() {}

    public static String hex(byte[] bytes) {
        Objects.requireNonNull(bytes, "bytes");
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            char[] output = new char[digest.length * 2];
            for (int i = 0; i < digest.length; i++) {
                int b = digest[i] & 0xff;
                output[i * 2] = HEX[b >>> 4];
                output[i * 2 + 1] = HEX[b & 15];
            }
            return new String(output);
        } catch (NoSuchAlgorithmException impossibleOnStandardJvm) {
            throw new IllegalStateException("SHA-256 unavailable", impossibleOnStandardJvm);
        }
    }
}
