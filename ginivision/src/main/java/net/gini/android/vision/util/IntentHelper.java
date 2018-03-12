package net.gini.android.vision.util;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.webkit.MimeTypeMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for {@link android.content.Intent}.
 */
public final class IntentHelper {

    /**
     * Retrieves the Uri from the Intent.
     *
     * @param intent an Intent
     * @return an Uri or null
     */
    @Nullable
    public static Uri getUri(@NonNull final Intent intent) {
        Uri uri = intent.getData();
        if (uri == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final ClipData clipData = intent.getClipData();
            if (clipData != null && clipData.getItemCount() > 0) {
                uri = clipData.getItemAt(0).getUri();
            }
        }
        return uri;
    }

    /**
     * Retrieves the available mime-types from the Intent.
     *
     * @param intent an Intent
     * @param context Android context
     * @return a list of mime-types or an empty list
     * @throws IllegalArgumentException if the Intent contains no Uri
     */
    @NonNull
    public static List<String> getMimeTypes(@NonNull final Intent intent,
            @NonNull final Context context) {
        final Uri data = getUri(intent);
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
            final ClipData clipData = intent.getClipData();
            if (clipData != null) {
                final ClipDescription description = clipData.getDescription();
                for (int i = 0; i < description.getMimeTypeCount(); i++) {
                    type = description.getMimeType(i);
                    mimeTypes.add(type);
                }
            }
        }
        return mimeTypes;
    }

    @Nullable
    public static List<Uri> getUris(@NonNull final Intent intent) {
        final ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            return uris;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            final ClipData clipData = intent.getClipData();
            if (clipData != null) {
                final int count = clipData.getItemCount();
                final ArrayList<Uri> clipDataUris = new ArrayList<>(count);
                for (int i = 0; i < count; i++) {
                    clipDataUris.add(clipData.getItemAt(i).getUri());
                }
                return clipDataUris;
            }
        }
        return null;
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
    private static String getMimeTypeFromUrl(@NonNull final String url) {
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getMimeTypeFromExtension(extension);
        }
        return null;
    }

    /**
     * Check whether the Intent has a specific mime-type.
     *
     * @param intent an Intent
     * @param context Android context
     * @param mimeType required mime-type string
     * @return {@code true}, if the Intent has the mime-type or {@code false}
     * @throws IllegalArgumentException if the Intent contains no Uri
     */
    public static boolean hasMimeType(@NonNull final Intent intent,
            @NonNull final Context context, @NonNull final String mimeType) {
        final List<String> mimeTypes = getMimeTypes(intent, context);
        for (final String type : mimeTypes) {
            if (type.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMimeType(@NonNull final Uri uri,
            @NonNull final Context context, @NonNull final String mimeType) {
        final String actualMimeType = getMimeType(uri, context);
        return actualMimeType != null && actualMimeType.equals(mimeType);
    }

    /**
     * Check whether the Intent has a mime-type starting the provided prefix.
     *
     * @param intent an Intent
     * @param context Android context
     * @param prefix mime-type prefix
     * @return {@code true}, if the Intent has a mime-type starting with the prefix or {@code false}
     * @throws IllegalArgumentException if the Intent contains no Uri
     */
    public static boolean hasMimeTypeWithPrefix(@NonNull final Intent intent,
            @NonNull final Context context, @NonNull final String prefix) {
        final List<String> mimeTypes = getMimeTypes(intent, context);
        for (final String type : mimeTypes) {
            if (type.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMimeTypeWithPrefix(@NonNull final Uri uri,
            @NonNull final Context context, @NonNull final String prefix) {
        final String actualMimeType = getMimeType(uri, context);
        return actualMimeType != null && actualMimeType.startsWith(prefix);
    }

    /**
     * Retrieve the name of the app from which the Intent was received.
     *
     * @param intent an Intent
     * @param context Android context
     * @return the app's name or null
     * @throws IllegalArgumentException if the Intent contains no Uri
     */
    @Nullable
    public static String getSourceAppName(@NonNull final Intent intent,
            @NonNull final Context context) {
        try {
            final ComponentName component = intent.getComponent();
            if (component == null) {
                return null;
            }
            final ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(component.getPackageName(), 0);
            return (String) context.getPackageManager().getApplicationLabel(appInfo);
        } catch (final PackageManager.NameNotFoundException e) {  // NOPMD
            // Ignore
        }
        return null;
    }

    public static boolean hasMultipleUris(@NonNull final Intent intent) {
        final List<Uri> uris = getUris(intent);
        return uris != null && uris.size() > 1;
    }

    private IntentHelper() {
    }
}
