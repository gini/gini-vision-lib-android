package net.gini.android.vision.review;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

/**
 * <h3>Component API</h3>
 *
 * <p>
 *     When you use the Compontent API with the Android Support Library, the {@code ReviewFragmentCompat} displays the photographed document and allows the user to review it by checking the sharpness, quality and orientation of the image. The user can correct the orientation by rotating the image.
 * </p>
 * <p>
 *     <b>Note:</b> Your Activity hosting this Fragment must extend the {@link android.support.v7.app.AppCompatActivity} and use an AppCompat Theme.
 * </p>
 * <p>
 *     Include the {@code ReviewFragmentCompat} into your layout by using the {@link ReviewFragmentCompat#createInstance(Document)} factory method to create an instance and display it using the {@link android.support.v4.app.FragmentManager}.
 * </p>
 * <p>
 *     A {@link ReviewFragmentListener} instance must be available until the {@code ReviewFragmentCompat} is attached to an activity. Failing to do so will throw an exception.
 *     The listener instance can be provided either implicitly by making the hosting Activity implement the {@link ReviewFragmentListener} interface or explicitly by
 *     setting the listener using {@link ReviewFragmentCompat#setListener(ReviewFragmentListener)}.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link ReviewFragmentCompat#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customizing the Review Screen</h3>
 *
 * <p>
 *     See the {@link ReviewActivity} for details.
 * </p>
 */
public class ReviewFragmentCompat extends Fragment implements FragmentImplCallback,
        ReviewFragmentInterface {

    @VisibleForTesting
    ReviewFragmentImpl mFragmentImpl;
    private ReviewFragmentListener mListener;

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment using the provided document.
     * </p>
     * <p>
     *     <b>Note:</b> Always use this method to create new instances. Document is required and an exception is thrown if it's missing.
     * </p>
     * @param document must be the {@link Document} from {@link CameraFragmentListener#onDocumentAvailable(Document)}
     * @return a new instance of the Fragment
     */
    public static ReviewFragmentCompat createInstance(@NonNull final Document document) {
        final ReviewFragmentCompat fragment = new ReviewFragmentCompat();
        fragment.setArguments(ReviewFragmentHelper.createArguments(document));
        return fragment;
    }

    @VisibleForTesting
    ReviewFragmentImpl getFragmentImpl() {
        return mFragmentImpl;
    }

    /**
     * @exclude
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = ReviewFragmentHelper.createFragmentImpl(this, getArguments());
        ReviewFragmentHelper.setListener(mFragmentImpl, getActivity(), mListener);
        mFragmentImpl.onCreate(savedInstanceState);
    }

    /**
     * @exclude
     */
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container,
            @Nullable final Bundle savedInstanceState) {
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
    public void onSaveInstanceState(final Bundle outState) {
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
