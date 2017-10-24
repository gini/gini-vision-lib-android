package net.gini.android.vision.util;

import static net.gini.android.vision.internal.util.StreamHelper.inputStreamToByteArray;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
    public static String getFilenameFromUri(@NonNull final Uri uri, @NonNull final Context context) {
        Cursor cursor = null;
        cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            throw new IllegalStateException("Could not retrieve a Cursor for the Uri");
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

    /**
     * Retrieves the file size of a Uri, if available.
     *
     * @param uri     a {@link Uri} pointing to a file
     * @param context Android context
     * @return the filename
     * @throws IllegalStateException if the Uri is not pointing to a file or the filename was not
     *                               available
     */
    public static int getFileSizeFromUri(@NonNull final Uri uri, @NonNull final Context context) {
        Cursor cursor = null;
        cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            throw new IllegalStateException("Could not retrieve a Cursor for the Uri");
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

}