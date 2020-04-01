package net.gini.android.vision.internal.util;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public final class StreamHelper {

    /**
     * Reads the input stream to a byte array.
     *
     * @param inputStream an open {@link InputStream} to read from, caller is responsible for
     *                    closing
     * @return byte array with input streams content
     * @throws IOException if the first byte cannot be read for any reason other than the end of the
     *                     file, if the input stream has been closed, or if some other I/O error
     *                     occurs.
     * @suppress
     */
    public static byte[] inputStreamToByteArray(@NonNull final InputStream inputStream)
            throws IOException {
        final int bufferSize = 8192;
        final ByteArrayOutputStream out = new ByteArrayOutputStream(bufferSize);
        final byte[] buffer = new byte[bufferSize];
        int nrRead;
        while ((nrRead = inputStream.read(buffer)) > 0) { // NOPMD
            out.write(buffer, 0, nrRead);
        }
        return out.toByteArray();
    }

    private StreamHelper() {
    }
}
