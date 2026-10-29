package com.goreecloud.android.core;

import java.util.Objects;
import java.util.regex.Pattern;

/** Single-component file names for app-private storage; never accepts an arbitrary path. */
public final class SafeFileName {
    private static final Pattern ALLOWED = Pattern.compile("[A-Za-z0-9][A-Za-z0-9._-]{0,127}");

    private SafeFileName() {}

    public static String require(String name) {
        Objects.requireNonNull(name, "name");
        if (!ALLOWED.matcher(name).matches() || name.equals(".") || name.equals("..")) {
            throw new IllegalArgumentException("Unsafe file name");
        }
        return name;
    }
}
