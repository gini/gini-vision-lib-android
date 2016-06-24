package net.gini.android.vision.reviewdocument;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.camera.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

/**
 * <p>
 *     When using the Compontent API the {@code ReviewDocumentFragmentCompat} displays the photographed document and allows the user to review it by checking the sharpness, quality and orientation of the image. The user can correct the orientation by rotating the image.
 * </p>
 * <p>
 *     <b>Note:</b> Your Activity hosting this Fragment must extend the {@link android.support.v7.app.AppCompatActivity} and use an AppCompat Theme.
 * </p>
 * <p>
 *     Include the {@code ReviewDocumentFragmentCompat} into your layout by using the {@link ReviewDocumentFragmentCompat#createInstance(Document)} factory method to create an instance and display it using the {@link android.support.v4.app.FragmentManager}.
 * </p>
 * <p>
 *     Your Activity must implement the {@link ReviewDocumentFragmentListener} interface to receive events from the Review Document Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link ReviewDocumentFragmentCompat#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customising the Review Screen</h3>
 *
 * <p>
 *     See the {@link ReviewDocumentActivity} for details.
 * </p>
 */
public class ReviewDocumentFragmentCompat extends Fragment implements FragmentImplCallback, ReviewDocumentFragmentInterface {

    private ReviewDocumentFragmentImpl mFragmentImpl;

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
    public static ReviewDocumentFragmentCompat createInstance(Document document) {
        ReviewDocumentFragmentCompat fragment = new ReviewDocumentFragmentCompat();
        fragment.setArguments(ReviewDocumentFragmentHelper.createArguments(document));
        return fragment;
    }

    /**
     * @exclude
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = ReviewDocumentFragmentHelper.createFragmentImpl(this, getArguments());
        ReviewDocumentFragmentHelper.setListener(mFragmentImpl, getActivity());
        mFragmentImpl.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mFragmentImpl.onCreateView(inflater, container, savedInstanceState);
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
