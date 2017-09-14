package net.gini.android.vision.internal.util;


import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @exclude
 */
public class StreamHelper {

    public static byte[] inputStreamToByteArray(@NonNull InputStream inputStream)
            throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[65536];
        int nrRead;
        while((nrRead = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, nrRead);
        }
        out.flush();
        return out.toByteArray();
    }
}
