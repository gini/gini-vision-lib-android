package net.gini.android.vision.camera;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.GiniVisionConfig;
import net.gini.android.vision.internal.permission.PermissionRequestListener;
import net.gini.android.vision.internal.permission.RuntimePermissions;

/**
 * <h3>Component API</h3>
 *
 * <p>
 * {@code CameraFragmentCompat} is the main entry point to the Gini Vision Library when using the
 * Component API with the Android Support Library.
 * </p>
 * <p>
 * It shows a camera preview with tap-to-focus functionality and a trigger button. The camera
 * preview also shows document corner guides to which the user should align the document.
 * </p>
 * <p>
 * <b>Note:</b> Your Activity hosting this Fragment must extend the {@link
 * android.support.v7.app.AppCompatActivity} and use an AppCompat Theme.
 * </p>
 * <p>
 * Include the {@code CameraFragmentCompat} into your layout either directly with {@code <fragment>}
 * in your Activity's layout or using the {@link android.support.v4.app.FragmentManager}.
 * </p>
 * <p>
 * Your Activity must implement the {@link CameraFragmentListener} interface to receive events from
 * the Camera Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 * Your Activity is automatically set as the listener in
 * {@link CameraFragmentCompat#onAttach(Context)}.
 * </p>
 *
 * <h3>Customizing the Camera Screen</h3>
 *
 * <p>
 * See the {@link CameraActivity} for details.
 * </p>
 */
public class CameraFragmentCompat extends Fragment implements CameraFragmentInterface,
        CameraFragmentImplCallback {

    public static CameraFragmentCompat createInstance(@NonNull final GiniVisionConfig giniVisionConfig) {
        CameraFragmentCompat fragment = new CameraFragmentCompat();
        fragment.setArguments(
                CameraFragmentHelper.createArguments(giniVisionConfig));
        return fragment;
    }

    private CameraFragmentImpl mFragmentImpl;
    private final RuntimePermissions mRuntimePermissions = new RuntimePermissions();

    /**
     * @exclude
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = CameraFragmentHelper.createFragmentImpl(this, getArguments());
        CameraFragmentHelper.setListener(mFragmentImpl, getActivity());
        mFragmentImpl.onCreate(savedInstanceState);
    }

    /**
     * @exclude
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * @exclude
     */
    @Override
    public void onStart() {
        super.onStart();
        mFragmentImpl.onStart();
    }

    /**
     * @exclude
     */
    @Override
    public void onStop() {
        super.onStop();
        mFragmentImpl.onStop();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        boolean handled = mFragmentImpl.onActivityResult(requestCode, resultCode, data);
        if (!handled) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
            @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        boolean handled = mRuntimePermissions.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (!handled) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void showDocumentCornerGuides() {
        mFragmentImpl.showDocumentCornerGuides();
    }

    @Override
    public void hideDocumentCornerGuides() {
        mFragmentImpl.hideDocumentCornerGuides();
    }

    @Override
    public void showCameraTriggerButton() {
        mFragmentImpl.showCameraTriggerButton();
    }

    @Override
    public void hideCameraTriggerButton() {
        mFragmentImpl.hideCameraTriggerButton();
    }

    @Override
    public void showInterface() {
        mFragmentImpl.showInterface();
    }

    @Override
    public void hideInterface() {
        mFragmentImpl.hideInterface();
    }

    @Override
    public void requestPermission(@NonNull final String permission,
            @NonNull final PermissionRequestListener listener) {
        mRuntimePermissions.requestPermission(this, permission, listener);
    }

    @Override
    public void showAlertDialog(@StringRes final int message,
            @StringRes final int positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveButtonClickListener)
                .show();
    }

    @Override
    public void showAlertDialog(@StringRes final int message,
            @StringRes final int positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @StringRes final int negativeButtonTitle) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveButtonClickListener)
                .setNegativeButton(negativeButtonTitle, null)
                .show();
    }
}
