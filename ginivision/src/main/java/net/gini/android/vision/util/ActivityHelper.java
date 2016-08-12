package net.gini.android.vision.util;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * @exclude
 */
public final class ActivityHelper {

    public static void enableHomeAsUp(AppCompatActivity activity) {
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static boolean handleMenuItemPressedForHomeButton(AppCompatActivity activity, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            activity.finish();
            return true;
        }
        return false;
    }

    public static <T> void setActivityExtra(Intent target, String extraKey, Context context, Class<T> activityClass) {
        target.putExtra(extraKey, new Intent(context, activityClass));
    }

    private ActivityHelper() {
    }
}
