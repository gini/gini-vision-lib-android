package net.gini.android.vision.internal.util;


import android.support.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @exclude
 */
class StreamHelper {

    /**
     * Reads the input stream to a byte array.
     *
     * @param inputStream an open {@link InputStream} to read from, caller is responsible for
     *                    closing
     * @return byte array with input streams content
     * @throws IOException if the first byte cannot be read for any reason other than the end of the
     *                     file, if the input stream has been closed, or if some other I/O error
     *                     occurs.
     */
    static byte[] inputStreamToByteArray(@NonNull InputStream inputStream)
            throws IOException {
        final int bufferSize = 8192;
        ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
        byte[] buffer = new byte[bufferSize];
        int nrRead;
        while ((nrRead = inputStream.read(buffer)) > 0) {
            out.write(buffer, 0, nrRead);
        }
        return out.toByteArray();
    }
}
