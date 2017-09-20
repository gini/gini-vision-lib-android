package net.gini.android.vision.internal.permission;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;

/**
 * @exclude
 */
class PermissionRequestFragmentStandard extends AbstractPermissionRequest<Fragment> {

    PermissionRequestFragmentStandard(@NonNull final String permission, final int reqCode,
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
        return FragmentCompat.shouldShowRequestPermissionRationale(fragment, getPermission());
    }

    @Override
    protected void doRequestPermission(@NonNull final Fragment fragment) {
        FragmentCompat.requestPermissions(fragment, new String[]{getPermission()}, getReqCode());
    }

}