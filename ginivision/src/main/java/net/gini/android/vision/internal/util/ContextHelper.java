package net.gini.android.vision.internal.util;

import android.content.Context;
import android.support.annotation.NonNull;

import net.gini.android.vision.R;

/**
 * @exclude
 */
public final class ContextHelper {

    /**
     * @exclude
     */
    public static String getClientApplicationId(@NonNull Context context) {
        return context.getPackageName();
    }

    /**
     * @exclude
     */
    public static boolean isTablet(@NonNull Context context) {
        return context.getResources().getBoolean(R.bool.gv_is_tablet);
    }

    private ContextHelper() {
    }
}
