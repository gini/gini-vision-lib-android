package net.gini.android.vision.internal.storage;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Alpar Szotyori on 20.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class ImageDiskStore {

    private static final Logger LOG = LoggerFactory.getLogger(ImageDiskStore.class);

    @Nullable
    public Uri save(@NonNull final Context context, @NonNull final byte[] bytes) {
        final Uri uri = generateUri(context);
        final File file = createFile(uri);
        if (file == null) {
            return null;
        }
        try {
            writeToFile(file, bytes);
            return uri;
        } catch (final IOException e) {
            LOG.error("Failed to write file", e);
            return null;
        }
    }

    @NonNull
    private Uri generateUri(@NonNull final Context context) {
        final Date now = new Date();
        final DateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmssS", Locale.US);
        final String filename = dateFormat.format(now);
        final String storePath = getStorePath(context);
        return new Uri.Builder().scheme("file").path(storePath).appendPath(filename).build();
    }

    @Nullable
    private File createFile(final Uri uri) {
        final File file = new File(uri.getPath());
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (final IOException e) {
                LOG.error("Failed to create file", e);
                return null;
            }
        }
        return file;
    }

    private void writeToFile(@NonNull final File file, @NonNull final byte[] bytes)
            throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(bytes);
            outputStream.flush();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (final IOException ignore) {
                }
            }
        }
    }

    public boolean update(@NonNull final Uri uri, @NonNull final byte[] bytes) {
        final File file = createFile(uri);
        if (file == null) {
            return false;
        }
        try {
            writeToFile(file, bytes);
            return true;
        } catch (final IOException e) {
            LOG.error("Failed to update file", e);
            return false;
        }
    }

    public void delete(@NonNull final Uri uri) {
        final File file = new File(uri.getPath());
        //noinspection ResultOfMethodCallIgnored
        file.delete();
    }

    public void clear(@NonNull final Context context) {
        final String storePath = getStorePath(context);
        final File storeDir = new File(storePath);
        if (!storeDir.isDirectory()) {
            return;
        }
        final File[] files = storeDir.listFiles();
        if (files != null) {
            for (final File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    @NonNull
    private String getStorePath(final @NonNull Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

}
