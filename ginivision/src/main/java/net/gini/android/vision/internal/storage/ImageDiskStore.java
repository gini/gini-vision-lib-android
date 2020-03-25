package net.gini.android.vision.internal.storage;

import static net.gini.android.vision.util.UriHelper.getFileExtension;

import android.content.Context;
import android.net.Uri;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

/**
 * Created by Alpar Szotyori on 20.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class ImageDiskStore {

    private static final Logger LOG = LoggerFactory.getLogger(ImageDiskStore.class);

    @VisibleForTesting
    static final String STORE_DIR = "gv-images";

    @Nullable
    public Uri save(@NonNull final Context context, @NonNull final byte[] bytes) {
        final Uri uri = generateUri(context, null);
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

    @Nullable
    public Uri save(@NonNull final Context context, @NonNull final Uri fromUri) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(fromUri);
            if (inputStream == null) {
                return null;
            }
            final String extension = getFileExtension(fromUri, context);
            final Uri uri = generateUri(context, extension);
            final File file = createFile(uri);
            if (file == null) {
                return null;
            }
            writeToFile(file, inputStream);
            return uri;
        } catch (final FileNotFoundException e) {
            LOG.error("Failed to open uri", e);
            return null;
        } catch (final IOException e) {
            LOG.error("Failed to write file", e);
            return null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    @NonNull
    private Uri generateUri(@NonNull final Context context, @Nullable final String extension) {
        final String filename =
                System.currentTimeMillis() + (extension != null ? "." + extension : "");
        final String storePath = getStoreDir(context).getAbsolutePath();
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

    @VisibleForTesting
    void writeToFile(@NonNull final File file, @NonNull final byte[] bytes)
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

    private void writeToFile(@NonNull final File file, @NonNull final InputStream inputStream)
            throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file), 65536);
            final byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
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

    public static void clear(@NonNull final Context context) {
        final File storeDir = getStoreDir(context);
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
        //noinspection ResultOfMethodCallIgnored
        storeDir.delete();
    }

    @NonNull
    private static File getStoreDir(@NonNull final Context context) {
        final File storeDir = new File(context.getFilesDir(), STORE_DIR);
        if (!storeDir.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                storeDir.mkdir();
            } catch (final SecurityException e) {
                LOG.error("Failed to create store folder", e);
            }
        }
        return storeDir;
    }

}
