package net.gini.android.vision;

import android.content.Context;
import android.content.Intent;

public class ActivityHelpers {

    public static <T> void setActivityExtra(Intent target, String extraKey, Context context, Class<T> activityClass) {
        target.putExtra(extraKey, new Intent(context, activityClass));
    }
}
