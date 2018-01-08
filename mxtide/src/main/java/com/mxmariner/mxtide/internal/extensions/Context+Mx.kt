package com.mxmariner.mxtide.internal.extensions

import android.content.Context
import android.support.annotation.RawRes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Copy the raw resource contents to a file in the [Context.getCacheDir]
 *
 * @param rawId id of raw resource file to copy
 * @return the copied file.
 */
internal fun Context.rawResourceAsCacheFile(@RawRes rawId: Int) : File {

    val destination = File(this.cacheDir, rawId.toString())

    destination.parentFile.takeUnless {
        it.exists()
    }?.mkdirs()

    destination.takeUnless {
        it.isFile
    }?.let {
        try {
            val inputStream = this.resources.openRawResource(rawId)
            val outputStream = BufferedOutputStream(
                    FileOutputStream(destination))
            val buffer = ByteArray(1024)
            var read = inputStream.read(buffer)
            while (read != -1) {
                outputStream.write(buffer, 0, read)
                read = inputStream.read(buffer)
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()
        } catch (e: IOException) {
        }
    }

    return destination
}
