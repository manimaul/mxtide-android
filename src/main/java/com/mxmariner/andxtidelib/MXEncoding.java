package com.mxmariner.andxtidelib;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MXEncoding {
    public static final String TAG = "MXEncoding";

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static String sha1sum(final File f) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-1");
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(f));
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = is.read(buffer);
                if (read > 0) {
                    digest.update(buffer, 0, read);
                }

            } while (read != -1);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException", e);
            return "";
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            return "";
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "NoSuchAlgorithmException", e);
            return "";
        }

        return bytesToHex(digest.digest());
    }

    public static String sha1sum(final String s) {
        final StringBuilder sb = new StringBuilder();
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            final byte[] array = md.digest(s.getBytes("UTF-8"));

            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (final Exception ignored) {
        }

        return sb.toString();
    }
}
