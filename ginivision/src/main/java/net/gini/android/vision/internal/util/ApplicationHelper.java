package net.gini.android.vision.internal.util;

import static net.gini.android.vision.internal.util.ContextHelper.getClientApplicationId;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;

/**
 * Created by Alpar Szotyori on 04.02.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * @exclude
 */
public final class ApplicationHelper {

    public static void startApplicationDetailsSettings(@Nullable final Activity activity) {
        // TODO: use overloaded one
        if (activity == null) {
            return;
        }
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        final Uri uri = Uri.fromParts("package", getClientApplicationId(activity), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static void startApplicationDetailsSettings(@NonNull final Application app) {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        final Uri uri = Uri.fromParts("package", getClientApplicationId(app), null);
        intent.setData(uri);
        app.startActivity(intent);
    }

    public static boolean isDefaultForMimeType(@Nullable final Activity activity,
            @NonNull final String mimeType) {
        // TODO: migrate to overloaded one
        if (activity == null) {
            return false;
        }
        final ArrayList<ComponentName> components = new ArrayList<>();
        final ArrayList<IntentFilter> filters = new ArrayList<>();
        final String packageName = ContextHelper.getClientApplicationId(activity);
        activity.getPackageManager().getPreferredActivities(filters, components, packageName);
        for (final IntentFilter filter : filters) {
            if (filter.hasDataType(mimeType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDefaultForMimeType(@NonNull final Application app,
            @NonNull final String mimeType) {
        final ArrayList<ComponentName> components = new ArrayList<>();
        final ArrayList<IntentFilter> filters = new ArrayList<>();
        final String packageName = ContextHelper.getClientApplicationId(app);
        app.getPackageManager().getPreferredActivities(filters, components, packageName);
        for (final IntentFilter filter : filters) {
            if (filter.hasDataType(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private ApplicationHelper() {
    }

}
