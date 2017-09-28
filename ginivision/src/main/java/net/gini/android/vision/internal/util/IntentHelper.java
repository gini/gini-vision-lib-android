package net.gini.android.vision.internal.util;

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
 * @exclude
 */
public final class IntentHelper {

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
        } catch (PackageManager.NameNotFoundException e) {
            // Ignore
        }
        return null;
    }
}
