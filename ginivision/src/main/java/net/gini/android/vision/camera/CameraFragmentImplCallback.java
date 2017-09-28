package net.gini.android.vision.camera;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import net.gini.android.vision.internal.permission.PermissionRequestListener;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

/**
 * @exclude
 */
interface CameraFragmentImplCallback extends FragmentImplCallback {
    void requestPermission(@NonNull final String permission,
            @NonNull final PermissionRequestListener listener);

    void showAlertDialog(@StringRes final int message,
            @StringRes final int positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener);

    void showAlertDialog(@StringRes final int message,
            @StringRes final int positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @StringRes final int negativeButtonTitle);
}
