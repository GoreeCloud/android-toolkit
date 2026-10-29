package com.goreecloud.android.toolkit.smoke;

import android.content.Context;
import android.content.Intent;
import com.goreecloud.android.core.RetryBackoff;
import com.goreecloud.android.core.SafeFileName;
import com.goreecloud.android.core.Sha256;
import com.goreecloud.android.core.TrustedEndpoint;
import com.goreecloud.android.kit.AndroidSettingsIntents;
import com.goreecloud.android.kit.AppScopedAtomicWriter;
import java.net.URI;
import java.nio.charset.StandardCharsets;

/** Compile-only API consumer verifying transitive Java and Android AAR symbols. */
public final class ConsumerContract {
    private ConsumerContract() {}

    public static boolean checkApiAvailability(Context context) {
        TrustedEndpoint endpoint = TrustedEndpoint.parse("https://cloud.example.com/dav/");
        boolean scoped = endpoint.contains(URI.create("https://cloud.example.com/dav/test"));
        long delay = new RetryBackoff(200L, 1000L).delayMillis(0);
        String safe = SafeFileName.require("example.json");
        String checksum = Sha256.hex("sample".getBytes(StandardCharsets.UTF_8));
        Intent intent = AndroidSettingsIntents.applicationDetails(context);
        AppScopedAtomicWriter writer = new AppScopedAtomicWriter(context, 1024);
        return scoped && delay == 200L && safe.equals("example.json")
                && checksum.length() == 64 && intent.getData() != null
                && writer != null;
    }
}
