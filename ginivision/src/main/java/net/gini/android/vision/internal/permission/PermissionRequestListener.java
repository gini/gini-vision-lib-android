package net.gini.android.vision.internal.permission;

import android.support.annotation.NonNull;

/**
 * @exclude
 */
public interface PermissionRequestListener {
    void permissionGranted();

    void permissionDenied();

    void shouldShowRequestPermissionRationale(@NonNull final RationaleResponse response);

    interface RationaleResponse {
        void requestPermission();
    }
}