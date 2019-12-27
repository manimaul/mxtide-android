package com.mxmariner.mxtide.internal.extensions

import android.content.Context
import androidx.annotation.RawRes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Copy the raw resource contents to a file in the [Context.getCacheDir]
 *
 * @param rawResName id of raw resource file to copy
 * @return the copied file.
 */
internal fun Context.rawResourceAsCacheFile(rawResName: String) : File {

    val destination = File(cacheDir, rawResName)

    val rawId = resources.getIdentifier(rawResName,
            "raw", packageName)

    destination.parentFile?.takeUnless {
        it.exists()
    }?.mkdirs()

    destination.takeUnless {
        it.isFile
    }?.let {
        this.resources.openRawResource(rawId).use { inputStream ->
            BufferedOutputStream(FileOutputStream(destination)).use { outputStream ->
                val buffer = ByteArray(1024)
                var read = inputStream.read(buffer)
                while (read != -1) {
                    outputStream.write(buffer, 0, read)
                    read = inputStream.read(buffer)
                }
            }
        }
    }

    return destination
}
