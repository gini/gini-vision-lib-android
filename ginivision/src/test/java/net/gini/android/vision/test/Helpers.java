package net.gini.android.vision.test;

import static androidx.test.InstrumentationRegistry.getTargetContext;

import android.content.res.AssetManager;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;

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
        final AssetManager assetManager = getTargetContext().getAssets();
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

    public static void copyAssetToStorage(@NonNull final String assetFilePath,
            @NonNull final String storageDirPath) throws IOException {
        final AssetManager assetManager = getTargetContext().getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = assetManager.open(assetFilePath);
            final File file = new File(storageDirPath,
                    Uri.parse(assetFilePath).getLastPathSegment());
            if (file.exists() || file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                copyFile(inputStream, outputStream);
            } else {
                throw new IOException("Could not create file: " + file.getAbsolutePath());
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private static void copyFile(final InputStream inputStream, final OutputStream outputStream)
            throws IOException {
        final byte[] buffer = new byte[8192];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
    }
}
