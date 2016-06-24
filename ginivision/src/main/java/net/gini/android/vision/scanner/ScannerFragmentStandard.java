package net.gini.android.vision.scanner;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>
 *     {@code ScannerFragmentStandard} is the main entry point to the Gini Vision Lib when using the Component API without the Android Support Library.
 * </p>
 * <p>
 *     It shows a camera preview with tap-to-focus functionality and a trigger button. The camera preview also shows document corner guides to which the user should align the document.
 * </p>
 * <p>
 *     Include the {@code ScannerFragmentStandard} into your layout either directly with {@code <fragment>} in your Activitie's layout or using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 *     Your Activity must implement the {@link ScannerFragmentListener} interface to receive events from the Scanner Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link ScannerFragmentStandard#onAttach(Context)}.
 * </p>
 *
 * <h3>Customising the Scanner Screen</h3>
 *
 * <p>
 *     See the {@link ScannerActivity} for details.
 * </p>
 */
public class ScannerFragmentStandard extends Fragment implements ScannerFragmentInterface {

    private final ScannerFragmentImpl mFragmentImpl = new ScannerFragmentImpl();

    /**
     * @exclude
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ScannerFragmentHelper.setListener(mFragmentImpl, context);
    }

    /**
     * @exclude
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onShowDocumentCornerGuides() {
        mFragmentImpl.onShowDocumentCornerGuides();
    }

    @Override
    public void onHideDocumentCornerGuides() {
        mFragmentImpl.onHideDocumentCornerGuides();
    }

    @Override
    public void onShowCameraTriggerButton() {
        mFragmentImpl.onShowCameraTriggerButton();
    }

    @Override
    public void onHideCameraTriggerButton() {
        mFragmentImpl.onHideCameraTriggerButton();
    }
}
