package net.gini.android.vision.internal.permission;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public interface PermissionRequestListener {
    void permissionGranted();

    void permissionDenied();

    void shouldShowRequestPermissionRationale(@NonNull final RationaleResponse response);

    /**
     * Internal use only.
     *
     * @suppress
     */
    interface RationaleResponse {
        void requestPermission();
    }
}