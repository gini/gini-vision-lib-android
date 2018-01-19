package net.gini.android.vision.internal.permission;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * @exclude
 */
public class RuntimePermissions {

    private final SparseArray<PermissionRequest> mPermissionRequests = new SparseArray<>();
    private int mPrevRequestCode;

    public RuntimePermissions() {
    }

    public void requestPermission(@NonNull final android.app.Fragment fragment,
            @NonNull final String permission, @NonNull final PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int requestCode = getNextRequestCode();
            final PermissionRequestFragmentStandard request = new PermissionRequestFragmentStandard(
                    permission, requestCode,
                    listener);
            mPermissionRequests.put(requestCode, request);
            request.requestPermission(fragment);
        } else {
            listener.permissionGranted();
        }
    }

    public void requestPermission(@NonNull final Fragment fragment,
            @NonNull final String permission,
            @NonNull final PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int requestCode = getNextRequestCode();
            final PermissionRequestFragmentCompat request = new PermissionRequestFragmentCompat(
                    permission, requestCode,
                    listener);
            mPermissionRequests.put(requestCode, request);
            request.requestPermission(fragment);
        } else {
            listener.permissionGranted();
        }
    }

    public boolean onRequestPermissionsResult(final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        final PermissionRequest request = mPermissionRequests.get(requestCode);
        if (request != null) {
            request.onRequestPermissionsResult(permissions, grantResults);
            mPermissionRequests.remove(requestCode);
            return true;
        }
        return false;
    }

    private int getNextRequestCode() {
        return ++mPrevRequestCode;
    }
}
