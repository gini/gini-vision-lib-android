package net.gini.android.vision.internal.util;

import static net.gini.android.vision.internal.util.StreamHelper.inputStreamToByteArray;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
        Uri data = getUri(intent);
        if (data == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(data);
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

    @Nullable
    public static Uri getUri(@NonNull final Intent intent) {
        Uri uri = intent.getData();
        if (uri == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clipData = intent.getClipData();
            if (clipData != null && clipData.getItemCount() > 0) {
                uri = clipData.getItemAt(0).getUri();
            }
        }
        return uri;
    }

    @NonNull
    public static List<String> getMimeTypes(@NonNull final Intent intent,
            @NonNull final Context context) {
        Uri data = getUri(intent);
        if (data == null) {
            throw new IllegalArgumentException("Intent data must contain a Uri");
        }
        final List<String> mimeTypes = new ArrayList<>();
        String type = context.getContentResolver().getType(data);
        if (type == null) {
            type = intent.getType();
        }
        if (type == null) {
            type = getMimeTypeFromUrl(data.getPath());
        }
        if (type != null) {
            mimeTypes.add(type);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clipData = intent.getClipData();
            if (clipData != null) {
                ClipDescription description = clipData.getDescription();
                for (int i = 0; i < description.getMimeTypeCount(); i++) {
                    type = description.getMimeType(i);
                    mimeTypes.add(type);
                }
            }
        }
        return mimeTypes;
    }

    @Nullable
    public static String getMimeTypeFromUrl(@NonNull final String url)
    {
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getMimeTypeFromExtension(extension);
        }
        return null;
    }

    public static boolean hasMimeType(@NonNull final Intent intent,
            @NonNull final Context context, @NonNull final String mimeType) {
        List<String> mimeTypes = getMimeTypes(intent, context);
        for (final String type : mimeTypes) {
            if (type.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMimeTypeWithPrefix(@NonNull final Intent intent,
            @NonNull final Context context, @NonNull final String prefix) {
        List<String> mimeTypes = getMimeTypes(intent, context);
        for (final String type : mimeTypes) {
            if (type.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
