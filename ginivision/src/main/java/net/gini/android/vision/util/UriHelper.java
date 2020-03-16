package net.gini.android.vision.util;

import static net.gini.android.vision.internal.util.StreamHelper.inputStreamToByteArray;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Helper methods for {@link android.net.Uri}.
 */
public final class UriHelper {

    /**
     * Reads the contents of the resource pointed to by the Uri into a byte array.
     *
     * @param uri     a {@link Uri} pointing to a file
     * @param context Android context
     * @return contents of the Uri
     * @throws IOException           if there is an issue with the input stream from the Uri
     * @throws IllegalStateException if null input stream was returned by the Context's Content
     *                               Resolver
     */
    @NonNull
    public static byte[] getBytesFromUri(@NonNull final Uri uri,
            @NonNull final Context context)
            throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            if (inputStream == null) {
                throw new IllegalStateException("Couldn't open input stream from intent data");
            }
            return inputStreamToByteArray(inputStream);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    /**
     * Test whether an InputStream can be opened for the Uri.
     *
     * @param uri     a {@link Uri} pointing to a file
     * @param context Android context
     * @return {@code true} if an InputStream can be opened for the Uri
     */
    public static boolean isUriInputStreamAvailable(@NonNull final Uri uri,
            @NonNull final Context context) {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            return inputStream != null;
        } catch (final IOException ignored) {
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException ignored) {
                }
            }
        }
        return false;
    }

    /**
     * Retrieves the filename of a Uri, if available.
     *
     * @param uri     a {@link Uri} pointing to a file
     * @param context Android context
     * @return the filename
     * @throws IllegalStateException if the Uri is not pointing to a file or the filename was not
     *                               available
     */
    @NonNull
    public static String getFilenameFromUri(@NonNull final Uri uri,
            @NonNull final Context context) {
        final Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            final String filename = getFilenameForPath(uri.getPath());
            if (TextUtils.isEmpty(filename)) {
                throw new IllegalStateException("Could not retrieve fhe filename");
            }
            return filename;
        }
        final int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        if (nameIndex == -1) {
            throw new IllegalStateException("Filename not available for the Uri");
        }
        cursor.moveToFirst();
        final String filename = cursor.getString(nameIndex);
        cursor.close();
        return filename;
    }

    @Nullable
    private static String getFilenameForPath(final String path) {
        final File file = new File(path);
        if (file.exists()) {
            return file.getName();
        }
        return null;
    }

    /**
     * Retrieves the file size of a Uri, if available.
     *
     * @param uri     a {@link Uri} pointing to a file
     * @param context Android context
     * @return the filename
     * @throws IllegalStateException if the Uri is not pointing to a file or the filesize was not
     *                               available
     */
    public static int getFileSizeFromUri(@NonNull final Uri uri, @NonNull final Context context) {
        final Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            final int size = getFileSizeForPath(uri.getPath());
            if (size < 0) {
                throw new IllegalStateException("Could not retrieve the file size");
            }
            return size;
        }
        final int nameIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        if (nameIndex == -1) {
            throw new IllegalStateException("File size not available for the Uri");
        }
        cursor.moveToFirst();
        final int fileSize = cursor.getInt(nameIndex);
        cursor.close();
        return fileSize;
    }

    private static int getFileSizeForPath(final String path) {
        final File file = new File(path);
        if (file.exists()) {
            return (int) file.length();
        }
        return -1;
    }

    @Nullable
    public static String getMimeType(@NonNull final Uri uri, @NonNull final Context context) {
        final String type = context.getContentResolver().getType(uri);
        if (type == null) {
            return getMimeTypeFromUrl(uri.getPath());
        }
        return type;
    }

    @Nullable
    public static String getMimeTypeFromUrl(@NonNull final String url) {
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getMimeTypeFromExtension(extension);
        }
        return null;
    }

    @Nullable
    public static String getFileExtension(@NonNull final Uri uri, @NonNull final Context context) {
        final String type = getMimeType(uri, context);
        if (type != null) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(type);
        }
        return null;
    }

    public static boolean hasMimeType(@NonNull final Uri uri,
            @NonNull final Context context, @NonNull final String mimeType) {
        final String actualMimeType = getMimeType(uri, context);
        return actualMimeType != null && actualMimeType.equals(mimeType);
    }

    private UriHelper() {
    }
}
