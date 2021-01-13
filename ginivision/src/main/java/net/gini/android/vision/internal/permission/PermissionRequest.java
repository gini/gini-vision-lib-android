package net.gini.android.vision.internal.permission;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
interface PermissionRequest<T> {

    void requestPermission(@NonNull final T context);

    void requestPermissionWithoutRationale(@NonNull final T context);

    void onRequestPermissionsResult(@NonNull final String[] permissions,
            @NonNull final int[] grantResults);
}
