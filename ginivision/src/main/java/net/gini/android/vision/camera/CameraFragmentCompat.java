package net.gini.android.vision.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

/**
 * <h3>Component API</h3>
 *
 * <p>
 *     {@code CameraFragmentCompat} is the main entry point to the Gini Vision Library when using the Component API with the Android Support Library.
 * </p>
 * <p>
 *     It shows a camera preview with tap-to-focus functionality and a trigger button. The camera preview also shows document corner guides to which the user should align the document.
 * </p>
 * <p>
 *     <b>Note:</b> Your Activity hosting this Fragment must extend the {@link android.support.v7.app.AppCompatActivity} and use an AppCompat Theme.
 * </p>
 * <p>
 *     Include the {@code CameraFragmentCompat} into your layout either directly with {@code <fragment>} in your Activity's layout or using the {@link android.support.v4.app.FragmentManager}.
 * </p>
 * <p>
 *     Your Activity must implement the {@link CameraFragmentListener} interface to receive events from the Camera Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link CameraFragmentCompat#onAttach(Context)}.
 * </p>
 *
 * <h3>Customizing the Camera Screen</h3>
 *
 * <p>
 *     See the {@link CameraActivity} for details.
 * </p>
 */
public class CameraFragmentCompat extends Fragment implements CameraFragmentInterface, FragmentImplCallback {

    private final CameraFragmentImpl mFragmentImpl = new CameraFragmentImpl(this);

    /**
     * @exclude
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        CameraFragmentHelper.setListener(mFragmentImpl, context);
    }

    /**
     * @exclude
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
}
