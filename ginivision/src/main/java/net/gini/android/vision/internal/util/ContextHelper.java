package net.gini.android.vision.internal.util;

import android.content.Context;
import android.support.annotation.NonNull;

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

    private ContextHelper() {
    }
}
