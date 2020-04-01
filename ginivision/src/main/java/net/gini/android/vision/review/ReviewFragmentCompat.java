package net.gini.android.vision.review;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.AlertDialogHelperCompat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;

/**
 * <h3>Component API</h3>
 *
 * <p> When you use the Compontent API with the Android Support Library, the {@code
 * ReviewFragmentCompat} displays the photographed or imported image and allows the user to review
 * it by checking the sharpness, quality and orientation of the image. The user can correct the
 * orientation by rotating the image.
 *
 * <p> <b>Note:</b> Your Activity hosting this Fragment must extend the {@link
 * androidx.appcompat.app.AppCompatActivity} and use an AppCompat Theme.
 *
 * <p> Include the {@code ReviewFragmentCompat} into your layout by using the {@link
 * ReviewFragmentCompat#createInstance(Document)} factory method to create an instance and display
 * it using the {@link androidx.fragment.app.FragmentManager}.
 *
 * <p> A {@link ReviewFragmentListener} instance must be available until the {@code
 * ReviewFragmentCompat} is attached to an activity. Failing to do so will throw an exception. The
 * listener instance can be provided either implicitly by making the hosting Activity implement the
 * {@link ReviewFragmentListener} interface or explicitly by setting the listener using {@link
 * ReviewFragmentCompat#setListener(ReviewFragmentListener)}.
 *
 * <p> Your Activity is automatically set as the listener in {@link ReviewFragmentCompat#onCreate(Bundle)}.
 *
 * <h3>Customizing the Review Screen</h3>
 *
 * See the {@link ReviewActivity} for details.
 */
public class ReviewFragmentCompat extends Fragment implements FragmentImplCallback,
        ReviewFragmentInterface {

    @VisibleForTesting
    ReviewFragmentImpl mFragmentImpl;
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
    public static ReviewFragmentCompat createInstance(@NonNull final Document document) {
        final ReviewFragmentCompat fragment = new ReviewFragmentCompat();
        fragment.setArguments(ReviewFragmentHelper.createArguments(document));
        return fragment;
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
        AlertDialogHelperCompat.showAlertDialog(activity, message, positiveButtonTitle,
                positiveButtonClickListener, negativeButtonTitle, negativeButtonClickListener,
                cancelListener);
    }

    @VisibleForTesting
    ReviewFragmentImpl getFragmentImpl() {
        return mFragmentImpl;
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
}
