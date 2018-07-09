package net.gini.android.vision.internal.util;

import static net.gini.android.vision.internal.util.ContextHelper.isTablet;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * @exclude
 */
public final class ActivityHelper {

    public static void enableHomeAsUp(final AppCompatActivity activity) {
        final ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static <T> void setActivityExtra(
            final Intent target, final String extraKey, final Context context,
            final Class<T> activityClass) {
        target.putExtra(extraKey, new Intent(context, activityClass));
    }

    public static void lockToPortraitOrientation(@Nullable final Activity activity) {
        if (activity == null) {
            return;
        }
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public static void forcePortraitOrientationOnPhones(@Nullable final Activity activity) {
        if (activity == null) {
            return;
        }
        if (!isTablet(activity)) {
            lockToPortraitOrientation(activity);
        }
    }

    private ActivityHelper() {
    }
}
