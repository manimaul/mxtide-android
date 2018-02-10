package com.mxmariner.tides.extensions

import android.content.res.Resources
import android.support.annotation.RawRes
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Copy a raw resource to a destination file.
 *
 * @param rawId id of raw resource file to copy
 * @param destination destination file (will not overwrite existing file)
 * @return if copy was successful or (non-directory) destination exists
 */
fun Resources.copyRawResourceToFile(@RawRes rawId: Int,
                                             destination: File) {

    destination.parentFile.takeUnless {
        it.exists()
    }?.mkdirs()

    destination.takeUnless {
        it.isDirectory || it.isFile
    }?.let {
        try {
            val inputStream = this.openRawResource(rawId)
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
}
