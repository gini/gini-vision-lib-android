package net.gini.android.vision.internal.permission;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

/**
 * @exclude
 */
public class RuntimePermissions {

    private final RequestCodeGenerator mRequestCodeGenerator = new RequestCodeGenerator();
    private final SparseArray<PermissionRequest> mPermissionRequests = new SparseArray<>();

    public RuntimePermissions() {
    }

    public void requestPermission(@NonNull final android.app.Fragment fragment,
            @NonNull final String permission, @NonNull final PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int requestCode = mRequestCodeGenerator.next();
            final PermissionRequestFragmentStandard request = new PermissionRequestFragmentStandard(
                    permission, requestCode,
                    listener);
            mPermissionRequests.put(requestCode, request);
            request.requestPermission(fragment);
        } else {
            listener.permissionGranted();
        }
    }

    public void requestPermission(@NonNull final Fragment fragment, @NonNull final String permission,
            @NonNull final PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int requestCode = mRequestCodeGenerator.next();
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

    private class RequestCodeGenerator {
        private int mPrevRequestCode = 0;

        int next() {
            return ++mPrevRequestCode;
        }
    }
}
