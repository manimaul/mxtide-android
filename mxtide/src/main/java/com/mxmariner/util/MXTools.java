package com.mxmariner.util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MXTools {
    public static final String TAG = MXTools.class.getSimpleName();

    /**
     *
     * @param context application context
     * @param assetSource asset path eg: assets/somefile.xxx
     * @param destination destination file (will not overwrite existing file)
     * @return if copy was successful or (non-directory) destination exists
     */
    public static boolean copyAssetFile(Context context, String assetSource, File destination) {
        if (destination.isDirectory()) {
            return false;
        }

        try {
            if (!destination.isFile()) {
                InputStream is = context.getAssets().open(assetSource);
                OutputStream os = new BufferedOutputStream(
                        new FileOutputStream(destination));
                byte[] buffer = new byte[1024];
                int read = is.read(buffer);
                while (read != -1) {
                    os.write(buffer, 0, read);
                    read = is.read(buffer);
                }
                os.close();
                os.flush();
                is.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "error copying file from assets", e);
            return false;
        }

        return true;
    }

    /**
     *
     * @param context application context
     * @param rawId id of raw resource file to copy
     * @param destination destination file (will not overwrite existing file)
     * @return if copy was successful or (non-directory) destination exists
     */
    public static boolean copyRawResourceFile(Context context, int rawId, File destination) {
        if (destination.isDirectory()) {
            return false;
        }

        try {
            if (!destination.isFile()) {
                InputStream is = context.getResources().openRawResource(rawId);
                OutputStream os = new BufferedOutputStream(
                        new FileOutputStream(destination));
                byte[] buffer = new byte[1024];
                int read = is.read(buffer);
                while (read != -1) {
                    os.write(buffer, 0, read);
                    read = is.read(buffer);
                }
                os.flush();
                os.close();
                is.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "error copying file from raw resources", e);
            return false;
        }

        return true;
    }
}
