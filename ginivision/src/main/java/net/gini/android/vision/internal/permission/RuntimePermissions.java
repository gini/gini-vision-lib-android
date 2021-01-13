package net.gini.android.vision.internal.permission;

import android.app.Activity;
import android.os.Build;
import android.util.SparseArray;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public class RuntimePermissions {

    private final SparseArray<PermissionRequest> mPermissionRequests = new SparseArray<>();
    private int mPrevRequestCode;

    public RuntimePermissions() {
    }

    public void requestPermission(@NonNull final Activity activity,
            @NonNull final String permission, @NonNull final PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int requestCode = getNextRequestCode();
            final PermissionRequestActivity request = new PermissionRequestActivity(permission,
                    requestCode, listener);
            mPermissionRequests.put(requestCode, request);
            request.requestPermission(activity);
        } else {
            listener.permissionGranted();
        }
    }

    public void requestPermissionWithoutRationale(@NonNull final Activity activity,
            @NonNull final String permission, @NonNull final PermissionRequestListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int requestCode = getNextRequestCode();
            final PermissionRequestActivity request = new PermissionRequestActivity(permission,
                    requestCode, listener);
            mPermissionRequests.put(requestCode, request);
            request.requestPermissionWithoutRationale(activity);
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
