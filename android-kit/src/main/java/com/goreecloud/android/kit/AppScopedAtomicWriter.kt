package com.goreecloud.android.kit

import android.content.Context
import com.goreecloud.android.core.SafeFileName
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.UUID

/** Bounded, app-private atomic writes. Not encrypted; do not write credentials or secrets. */
class AppScopedAtomicWriter(context: Context, private val maxBytes: Int = 1_048_576) {
    private val root = context.applicationContext.filesDir

    init {
        require(maxBytes in 1..10_485_760) { "maxBytes must be between 1 byte and 10 MiB" }
    }

    fun write(name: String, content: ByteArray): File {
        val safeName = SafeFileName.require(name)
        require(content.size <= maxBytes) { "Content is larger than the configured limit" }
        val target = File(root, safeName)
        val temporary = File(root, "$safeName.${UUID.randomUUID()}.tmp")
        try {
            FileOutputStream(temporary).use { out ->
                out.write(content)
                out.fd.sync()
            }
            // Fail closed when the filesystem cannot provide atomic replacement.
            Files.move(
                temporary.toPath(),
                target.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING,
            )
            return target
        } finally {
            if (temporary.exists()) temporary.delete()
        }
    }
}
