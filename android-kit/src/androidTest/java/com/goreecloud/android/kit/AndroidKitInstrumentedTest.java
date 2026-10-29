package com.goreecloud.android.kit;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Test;
import org.junit.runner.RunWith;

/** Exercises real app-private Android storage and native settings Intent construction. */
@RunWith(AndroidJUnit4.class)
public final class AndroidKitInstrumentedTest {
    private Context context() {
        return InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    private String name() {
        return "toolkit-" + System.nanoTime() + ".txt";
    }

    @Test
    public void writesAndReplacesPrivateFile() throws Exception {
        Context context = context();
        AppScopedAtomicWriter writer = new AppScopedAtomicWriter(context, 32);
        String name = name();
        File target = new File(context.getFilesDir(), name);
        try {
            byte[] first = "before".getBytes(StandardCharsets.UTF_8);
            byte[] second = "after!".getBytes(StandardCharsets.UTF_8);
            assertEquals(target.getCanonicalPath(), writer.write(name, first).getCanonicalPath());
            assertArrayEquals(first, Files.readAllBytes(target.toPath()));
            writer.write(name, second);
            assertArrayEquals(second, Files.readAllBytes(target.toPath()));
            assertEquals(context.getFilesDir().getCanonicalPath(), target.getParentFile().getCanonicalPath());
        } finally {
            assertTrue(!target.exists() || target.delete());
        }
    }

    @Test
    public void oversizeDoesNotReplaceExistingFile() throws Exception {
        Context context = context();
        AppScopedAtomicWriter writer = new AppScopedAtomicWriter(context, 4);
        String name = name();
        File target = new File(context.getFilesDir(), name);
        try {
            byte[] original = {1, 2, 3, 4};
            writer.write(name, original);
            assertThrows(IllegalArgumentException.class, () -> writer.write(name, new byte[5]));
            assertArrayEquals(original, Files.readAllBytes(target.toPath()));
        } finally {
            assertTrue(!target.exists() || target.delete());
        }
    }

    @Test
    public void rejectsInvalidNamesAndLimits() {
        AppScopedAtomicWriter writer = new AppScopedAtomicWriter(context(), 4);
        byte[] data = {1};
        assertThrows(IllegalArgumentException.class, () -> writer.write("../outside", data));
        assertThrows(IllegalArgumentException.class, () -> writer.write("folder/child", data));
        assertThrows(IllegalArgumentException.class, () -> writer.write(".hidden", data));
        assertThrows(IllegalArgumentException.class, () -> new AppScopedAtomicWriter(context(), 0));
        assertThrows(IllegalArgumentException.class, () -> new AppScopedAtomicWriter(context(), 10_485_761));
    }

    @Test
    public void createsPackageSpecificSettingsIntentWithoutLaunching() {
        Context context = context();
        Intent intent = AndroidSettingsIntents.applicationDetails(context);
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, intent.getAction());
        assertNotNull(intent.getData());
        assertEquals("package", intent.getData().getScheme());
        assertEquals(context.getPackageName(), intent.getData().getSchemeSpecificPart());
        assertFalse(intent.hasExtra("token"));
    }
}
