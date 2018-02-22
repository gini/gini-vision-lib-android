package net.gini.android.vision.review;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
 *     When you use the Compontent API without the Android Support Library, the {@code ReviewFragmentStandard} displays the photographed document and allows the user to review it by checking the sharpness, quality and orientation of the image. The user can correct the orientation by rotating the image.
 * </p>
 * <p>
 *     Include the {@code ReviewFragmentStandard} into your layout by using the {@link ReviewFragmentStandard#createInstance(Document)} factory method to create an instance and display it using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 *     A {@link ReviewFragmentListener} instance must be available until the {@code ReviewFragmentStandard} is attached to an activity. Failing to do so will throw an exception.
 *     The listener instance can be provided either implicitly by making the hosting Activity implement the {@link ReviewFragmentListener} interface or explicitly by
 *     setting the listener using {@link ReviewFragmentStandard#setListener(ReviewFragmentListener)}.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link ReviewFragmentStandard#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customizing the Review Screen</h3>
 *
 * <p>
 *     See the {@link ReviewActivity} for details.
 * </p>
 */
public class ReviewFragmentStandard extends Fragment implements FragmentImplCallback,
        ReviewFragmentInterface {

    private ReviewFragmentImpl mFragmentImpl;
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
    public static ReviewFragmentStandard createInstance(final Document document) {
        final ReviewFragmentStandard fragment = new ReviewFragmentStandard();
        fragment.setArguments(ReviewFragmentHelper.createArguments(document));
        return fragment;
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
