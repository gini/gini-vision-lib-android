package net.gini.android.vision.review;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.AlertDialogHelperStandard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <h3>Component API</h3>
 *
 * <p> When you use the Compontent API without the Android Support Library, the {@code
 * ReviewFragmentStandard} displays the photographed or imported image and allows the user to review
 * it by checking the sharpness, quality and orientation of the image. The user can correct the
 * orientation by rotating the image.
 *
 * <p> Include the {@code ReviewFragmentStandard} into your layout by using the {@link
 * ReviewFragmentStandard#createInstance(Document)} factory method to create an instance and display
 * it using the {@link android.app.FragmentManager}.
 *
 * <p> A {@link ReviewFragmentListener} instance must be available until the {@code
 * ReviewFragmentStandard} is attached to an activity. Failing to do so will throw an exception. The
 * listener instance can be provided either implicitly by making the hosting Activity implement the
 * {@link ReviewFragmentListener} interface or explicitly by setting the listener using {@link
 * ReviewFragmentStandard#setListener(ReviewFragmentListener)}.
 *
 * <p> Your Activity is automatically set as the listener in {@link ReviewFragmentStandard#onCreate(Bundle)}.
 *
 * <h3>Customizing the Review Screen</h3>
 *
 * See the {@link ReviewActivity} for details.
 */
public class ReviewFragmentStandard extends Fragment implements FragmentImplCallback,
        ReviewFragmentInterface {

    private ReviewFragmentImpl mFragmentImpl;
    private ReviewFragmentListener mListener;

    /**
     * <p> Factory method for creating a new instance of the Fragment using the provided document.
     *
     * <p> <b>Note:</b> Always use this method to create new instances. Document is required and an
     * exception is thrown if it's missing.
     *
     * @param document must be the {@link Document} from {@link CameraFragmentListener#onDocumentAvailable(Document)}
     *
     * @return a new instance of the Fragment
     */
    public static ReviewFragmentStandard createInstance(final Document document) {
        final ReviewFragmentStandard fragment = new ReviewFragmentStandard();
        fragment.setArguments(ReviewFragmentHelper.createArguments(document));
        return fragment;
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = ReviewFragmentHelper.createFragmentImpl(this, getArguments());
        ReviewFragmentHelper.setListener(mFragmentImpl, getActivity(), mListener);
        mFragmentImpl.onCreate(savedInstanceState);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void onStart() {
        super.onStart();
        mFragmentImpl.onStart();
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void onStop() {
        super.onStop();
        mFragmentImpl.onStop();
    }

    @Override
    public void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        mFragmentImpl.onSaveInstanceState(outState);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mFragmentImpl.onDestroy();
        mFragmentImpl = null;  // NOPMD
    }

    @Override
    public void onDocumentAnalyzed() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.onDocumentAnalyzed();
    }

    @Override
    public void onNoExtractionsFound() {
        mFragmentImpl.onNoExtractionsFound();
    }

    @Override
    public void setListener(@NonNull final ReviewFragmentListener listener) {
        if (mFragmentImpl != null) {
            mFragmentImpl.setListener(listener);
        }
        mListener = listener;
    }

    @Override
    public void showAlertDialog(@NonNull final String message,
            @NonNull final String positiveButtonTitle,
            @NonNull final DialogInterface.OnClickListener positiveButtonClickListener,
            @Nullable final String negativeButtonTitle,
            @Nullable final DialogInterface.OnClickListener negativeButtonClickListener,
            @Nullable final DialogInterface.OnCancelListener cancelListener) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        AlertDialogHelperStandard.showAlertDialog(activity, message, positiveButtonTitle,
                positiveButtonClickListener, negativeButtonTitle, negativeButtonClickListener,
                cancelListener);
    }
}
