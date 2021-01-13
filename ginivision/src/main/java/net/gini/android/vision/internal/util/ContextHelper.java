package net.gini.android.vision.internal.util;

import android.content.Context;

import net.gini.android.vision.R;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public final class ContextHelper {

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static String getClientApplicationId(@NonNull final Context context) {
        return context.getPackageName();
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static boolean isTablet(@NonNull final Context context) {
        return context.getResources().getBoolean(R.bool.gv_is_tablet);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static boolean isPortraitOrientation(@NonNull final Context context) {
        return context.getResources().getBoolean(R.bool.gv_is_portrait);
    }

    private ContextHelper() {
    }
}
