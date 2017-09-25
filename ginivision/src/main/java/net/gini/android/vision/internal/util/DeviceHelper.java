package net.gini.android.vision.internal.util;

import android.app.Activity;

/**
 * @exclude
 */
public final class DeviceHelper {

    public static String getDeviceOrientation(Activity activity) {
        return ContextHelper.isPortraitOrientation(activity) ? "portrait" : "landscape";
    }

    public static String getDeviceType(Activity activity) {
        return ContextHelper.isTablet(activity) ? "tablet" : "phone";
    }
}
