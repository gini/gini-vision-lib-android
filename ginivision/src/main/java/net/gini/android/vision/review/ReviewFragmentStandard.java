package net.gini.android.vision.review;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.Document;
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
 *     Your Activity must implement the {@link ReviewFragmentListener} interface to receive events from the Review Document Fragment. Failing to do so will throw an exception.
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
public class ReviewFragmentStandard extends Fragment implements FragmentImplCallback, ReviewFragmentInterface {

    private ReviewFragmentImpl mFragmentImpl;

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
    public static ReviewFragmentStandard createInstance(Document document) {
        ReviewFragmentStandard fragment = new ReviewFragmentStandard();
        fragment.setArguments(ReviewFragmentHelper.createArguments(document));
        return fragment;
    }

    /**
     * @exclude
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = ReviewFragmentHelper.createFragmentImpl(this, getArguments());
        ReviewFragmentHelper.setListener(mFragmentImpl, getActivity());
        mFragmentImpl.onCreate(savedInstanceState);
    }

    /**
     * @exclude
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
     *
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
    public void onDestroy() {
        super.onDestroy();
        mFragmentImpl.onDestroy();
        mFragmentImpl = null;
    }

    @Override
    public void onDocumentAnalyzed() {
        if (mFragmentImpl == null) {
            return;
        }
        mFragmentImpl.onDocumentAnalyzed();
    }
}
