package net.gini.android.vision.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.internal.permission.PermissionRequestListener;
import net.gini.android.vision.internal.permission.RuntimePermissions;

/**
 * <h3>Component API</h3>
 *
 * <p>
 *     {@code CameraFragmentStandard} is the main entry point to the Gini Vision Library when using the Component API without the Android Support Library.
 * </p>
 * <p>
 *     It shows a camera preview with tap-to-focus functionality and a trigger button. The camera preview also shows document corner guides to which the user should align the document.
 * </p>
 * <p>
 *     If instantiated with {@link CameraFragmentStandard#createInstance(GiniVisionFeatureConfiguration)} then a button for importing documents is shown next to the trigger button. A hint popup is displayed the first time the Gini Vision Library is used to inform the user about document importing.
 * </p>
 * <p>
 *     For importing documents {@code READ_EXTERNAL_STORAGE} permission is required and if the permission is not granted the Gini Vision Library will prompt the user to grant the permission. See @{code Customizing the Camera Screen} on how to override the message and button titles for the rationale and on permission denial alerts.
 * </p>
 * <p>
 *     Include the {@code CameraFragmentStandard} into your layout either directly with {@code <fragment>} in your Activity's layout or using the {@link android.app.FragmentManager} and one of the {@code createInstance()} methods.
 * </p>
 * <p>
 *     A {@link CameraFragmentListener} instance must be available until the {@code CameraFragmentStandard} is attached to an activity. Failing to do so will throw an exception.
 *     The listener instance can be provided either implicitly by making the hosting Activity implement the {@link CameraFragmentListener} interface or explicitly by
 *     setting the listener using {@link CameraFragmentCompat#setListener(CameraFragmentListener)}.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link CameraFragmentStandard#onAttach(Context)}.
 * </p>
 *
 * <h3>Customizing the Camera Screen</h3>
 *
 * <p>
 *     See the {@link CameraActivity} for details.
 * </p>
 */
public class CameraFragmentStandard extends Fragment implements CameraFragmentInterface,
        CameraFragmentImplCallback {

    private CameraFragmentListener mListener;

    public static CameraFragmentStandard createInstance() {
        return new CameraFragmentStandard();
    }

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment with document import enabled for the specified file types.
     * </p>
     * @param giniVisionFeatureConfiguration feature configuration
     * @return a new instance of the Fragment
     */
    public static CameraFragmentStandard createInstance(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        final CameraFragmentStandard fragment = new CameraFragmentStandard();
        fragment.setArguments(
                CameraFragmentHelper.createArguments(giniVisionFeatureConfiguration));
        return fragment;
    }

    private CameraFragmentImpl mFragmentImpl;
    private final RuntimePermissions mRuntimePermissions = new RuntimePermissions();

    /**
     * @exclude
     */
    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        mFragmentImpl = new CameraFragmentHelper().createFragmentImpl(this, getArguments());
        CameraFragmentHelper.setListener(mFragmentImpl, context, mListener);
    }

    /**
     * @exclude
     */
    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return;
        }
        mFragmentImpl = new CameraFragmentHelper().createFragmentImpl(this, getArguments());
        CameraFragmentHelper.setListener(mFragmentImpl, activity, mListener);
    }

    /**
     * @exclude
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl.onCreate(savedInstanceState);
    }

    /**
     * @exclude
     */
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
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
    public void onResume() {
        super.onResume();
        mFragmentImpl.onResume();
    }

    /**
     * @exclude
     */
    @Override
    public void onStop() {
        super.onStop();
        mFragmentImpl.onStop();
    }

    /**
     * @exclude
     */
    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        mFragmentImpl.onSaveInstanceState(outState);
    }

    /**
     * @exclude
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentImpl.onDestroy();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        final boolean handled = mFragmentImpl.onActivityResult(requestCode, resultCode, data);
        if (!handled) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode,
            @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        final boolean handled = mRuntimePermissions.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);
        if (!handled) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void setListener(@NonNull final CameraFragmentListener listener) {
        if (mFragmentImpl != null) {
            mFragmentImpl.setListener(listener);
        }
        mListener = listener;
    }

    @Deprecated
    @Override
    public void showDocumentCornerGuides() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.showDocumentCornerGuides();
    }

    @Deprecated
    @Override
    public void hideDocumentCornerGuides() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.hideDocumentCornerGuides();
    }

    @Deprecated
    @Override
    public void showCameraTriggerButton() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.showCameraTriggerButton();
    }

    @Deprecated
    @Override
    public void hideCameraTriggerButton() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.hideCameraTriggerButton();
    }

    @Override
    public void showInterface() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.showInterface();
    }

    @Override
    public void hideInterface() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.hideInterface();
    }

    @Override
    public void showActivityIndicatorAndDisableInteraction() {
        mFragmentImpl.showActivityIndicatorAndDisableInteraction();
    }

    @Override
    public void hideActivityIndicatorAndEnableInteraction() {
        mFragmentImpl.hideActivityIndicatorAndEnableInteraction();
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        mFragmentImpl.showError(message, duration);
    }

    @Override
    public void requestPermission(@NonNull final String permission,
            @NonNull final PermissionRequestListener listener) {
        mRuntimePermissions.requestPermission(this, permission, listener);
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
        showAlertDialog(activity.getString(message), positiveButtonTitle,
                positiveButtonClickListener, negativeButtonTitle);
    }

    @Override
    public void showAlertDialog(@NonNull final String message,
            @StringRes final int positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @StringRes final int negativeButtonTitle) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton(positiveButtonTitle, positiveButtonClickListener)
                .setNegativeButton(negativeButtonTitle, null)
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
