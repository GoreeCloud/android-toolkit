package com.goreecloud.android.toolkit.independent;

import android.content.Context;
import android.content.Intent;
import com.goreecloud.android.core.RetryBackoff;
import com.goreecloud.android.core.Sha256;
import com.goreecloud.android.core.TrustedEndpoint;
import com.goreecloud.android.kit.AndroidSettingsIntents;
import com.goreecloud.android.kit.AppScopedAtomicWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/**
 * Compile-only independent consumer; no launcher, app permissions,
 * network activity, credentials, or installation requirement.
 */
public final class Contract {
    private Contract() {}

    public static boolean publicApiCompiles(Context context) {
        TrustedEndpoint endpoint = TrustedEndpoint.parse("https://example.com/dav/");
        long waitMillis = new RetryBackoff(100, 1000).delayMillis(0);
        String hash = Sha256.hex("fixture".getBytes(StandardCharsets.UTF_8));
        Intent settings = AndroidSettingsIntents.applicationDetails(context);
        AppScopedAtomicWriter writer = new AppScopedAtomicWriter(context, 1024);
        return endpoint.contains(URI.create("https://example.com/dav/file"))
                && waitMillis == 100 && hash.length() == 64
                && settings.getData() != null && writer != null;
    }
}
