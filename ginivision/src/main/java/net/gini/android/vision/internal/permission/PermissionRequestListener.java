package net.gini.android.vision.internal.permission;

import androidx.annotation.NonNull;

/**
 * @exclude
 */
public interface PermissionRequestListener {
    void permissionGranted();

    void permissionDenied();

    void shouldShowRequestPermissionRationale(@NonNull final RationaleResponse response);

    /**
     * @exclude
     */
    interface RationaleResponse {
        void requestPermission();
    }
}