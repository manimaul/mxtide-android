package com.mxmariner.util

import android.content.Context
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

private const val TAG = "MXTools"

/**
 * @param context     application context
 * @param rawId       id of raw resource file to copy
 * @param destination destination file (will not overwrite existing file)
 */
fun copyRawResourceFile(context: Context, rawId: Int, destination: File) {
    if (!destination.isDirectory) {
        try {
            if (!destination.isFile) {
                val inputStream = context.resources.openRawResource(rawId)
                val outputStream = BufferedOutputStream(FileOutputStream(destination))
                val buffer = ByteArray(1024)
                var read = inputStream.read(buffer)
                while (read != -1) {
                    outputStream.write(buffer, 0, read)
                    read = inputStream.read(buffer)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
            }
        } catch (e: IOException) {
            Log.e(TAG, "error copying file from raw resources", e)
        }
    }
}
