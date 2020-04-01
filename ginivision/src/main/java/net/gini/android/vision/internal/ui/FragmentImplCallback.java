package net.gini.android.vision.internal.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * @suppress
 */
public interface FragmentImplCallback {

    @Nullable
    Activity getActivity();

    @Nullable
    View getView();

    void startActivity(Intent intent);

    void startActivityForResult(Intent intent, int requestCode);

    void showAlertDialog(@NonNull final String message,
            @NonNull final String positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @Nullable final String negativeButtonTitle,
            @Nullable final DialogInterface.OnClickListener negativeButtonClickListener,
            @Nullable final DialogInterface.OnCancelListener cancelListener);
}
