package net.gini.android.vision.internal.util;

import static net.gini.android.vision.internal.util.StreamHelper.inputStreamToByteArray;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @exclude
 */
public final class UriHelper {

    /**
     * Reads the contents of the resources pointed to by Uri into a byte array.
     *
     * @param uri     a {@link Uri} pointing to a file
     * @param context Android context
     * @return contents of the Uri
     * @throws IOException           if there is an issue with the input stream from the Uri
     * @throws IllegalStateException if null input stream was returned by the Context's Content
     *                               Resolver
     */
    @NonNull
    static byte[] getBytesFromUri(@NonNull final Uri uri,
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
        final int nameIndex = cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();
        final String filename = cursor.getString(nameIndex);
        cursor.close();
        return filename;
    }

}
