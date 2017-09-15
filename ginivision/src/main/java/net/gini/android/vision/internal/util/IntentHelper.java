package net.gini.android.vision.internal.util;

import static net.gini.android.vision.internal.util.StreamHelper.inputStreamToByteArray;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

/**
 * @exclude
 */
public final class IntentHelper {

    /**
     * Reads the contents of the resources pointed to by the Intent's data Uri into a byte array.
     *
     * @param intent  an {@link Intent} containing a {@link Uri}
     * @param context Android context
     * @return contents of the Uri
     * @throws IOException              if there is an issue with the input stream from the Uri
     * @throws IllegalArgumentException if the Intent's data is null
     * @throws IllegalStateException    if null input stream was returned by the Context's Content
     *                                  Resolver
     */
    @NonNull
    public static byte[] getBytesFromIntentUri(@NonNull final Intent intent,
            @NonNull final Context context)
            throws IOException {
        if (intent.getData() == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(intent.getData());
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
}
