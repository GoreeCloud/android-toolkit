package com.goreecloud.android.kit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/** Native settings navigation; returns an Intent instead of launching an activity implicitly. */
object AndroidSettingsIntents {
    @JvmStatic
    fun applicationDetails(context: Context): Intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
}
