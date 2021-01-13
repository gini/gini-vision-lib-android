package net.gini.android.vision.internal.util;

import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public final class DeviceHelper {

    public static String getDeviceOrientation(@NonNull final Context context) {
        return ContextHelper.isPortraitOrientation(context) ? "portrait" : "landscape";
    }

    public static String getDeviceType(@NonNull final Context context) {
        return ContextHelper.isTablet(context) ? "tablet" : "phone";
    }

    private DeviceHelper() {
    }
}
