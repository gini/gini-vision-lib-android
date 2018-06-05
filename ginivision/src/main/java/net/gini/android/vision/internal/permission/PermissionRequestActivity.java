package net.gini.android.vision.internal.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;

/**
 * @exclude
 */
class PermissionRequestActivity extends AbstractPermissionRequest<Activity> {

    PermissionRequestActivity(@NonNull final String permission, final int reqCode,
            @NonNull final PermissionRequestListener listener) {
        super(permission, reqCode, listener);
    }

    @Override
    protected Boolean checkSelfPermission(@NonNull final Activity activity) {
        return ContextCompat.checkSelfPermission(activity, getPermission())
                == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected boolean shouldShowRequestRationale(@NonNull final Activity activity) {
        return activity.shouldShowRequestPermissionRationale(getPermission());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void doRequestPermission(@NonNull final Activity activity) {
        activity.requestPermissions(new String[]{getPermission()}, getReqCode());
    }

}