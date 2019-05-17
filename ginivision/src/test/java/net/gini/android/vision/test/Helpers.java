package net.gini.android.vision.test;

import android.content.res.AssetManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import androidx.test.InstrumentationRegistry;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public final class Helpers {

    private Helpers() {
    }

    public static byte[] getTestJpeg() throws IOException {
        return loadAsset("invoice.jpg");
    }

    public static byte[] loadAsset(final String filename) throws IOException {
        final AssetManager assetManager = InstrumentationRegistry.getTargetContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(filename);
            return inputStreamToByteArray(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private static byte[] inputStreamToByteArray(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes;
        try {
            final byte[] buffer = new byte[8192];
            int readBytes;
            while ((readBytes = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readBytes);
            }
            bytes = outputStream.toByteArray();
        } finally {
            outputStream.close();
        }
        return bytes;
    }
}
