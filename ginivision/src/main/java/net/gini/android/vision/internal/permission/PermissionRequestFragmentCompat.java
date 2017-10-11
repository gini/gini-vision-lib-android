package net.gini.android.vision.internal.permission;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * @exclude
 */
class PermissionRequestFragmentCompat extends AbstractPermissionRequest<Fragment> {

    PermissionRequestFragmentCompat(@NonNull final String permission, final int reqCode,
            @NonNull final PermissionRequestListener listener) {
        super(permission, reqCode, listener);
    }

    @Override
    protected Boolean checkSelfPermission(@NonNull final Fragment fragment) {
        final Activity activity = fragment.getActivity();
        if (activity == null) {
            return false;
        }
        return ContextCompat.checkSelfPermission(activity, getPermission())
                == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected boolean shouldShowRequestRationale(@NonNull final Fragment fragment) {
        return fragment.shouldShowRequestPermissionRationale(getPermission());
    }

    @Override
    protected void doRequestPermission(@NonNull final Fragment fragment) {
        fragment.requestPermissions(new String[]{getPermission()}, getReqCode());
    }

}