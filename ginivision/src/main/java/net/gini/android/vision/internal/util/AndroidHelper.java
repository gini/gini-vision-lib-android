package net.gini.android.vision.internal.util;

import android.os.Build;

/**
 * Internal use only.
 *
 * @suppress
 */
public final class AndroidHelper {

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static boolean isMarshmallowOrLater() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    private AndroidHelper() {
    }
}
