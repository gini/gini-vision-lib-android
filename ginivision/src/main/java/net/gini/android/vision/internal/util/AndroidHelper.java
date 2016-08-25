package net.gini.android.vision.internal.util;

import android.os.Build;

/**
 * @exclude
 */
public final class AndroidHelper {

    /**
     * @exclude
     */
    public static boolean isMarshmallowOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private AndroidHelper() {
    }
}
