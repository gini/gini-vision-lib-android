package net.gini.android.vision.analyze;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

/**
 * <p>
 *     When using the Component API the {@code AnalyzeDocumentFragmentStandard} displays the captured document and an activity indicator while the document is being analyzed by the Gini API.
 * </p>
 * <p>
 *     Include the {@code AnalyzeDocumentFragmentStandard} into your layout by using the {@link AnalyzeDocumentFragmentStandard#createInstance(Document)} factory method to create an instance and display it using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 *     Your Activity must implement the {@link AnalyzeDocumentFragmentListener} interface to receive events from the Analyze Document Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link AnalyzeDocumentFragmentStandard#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customising the Analysis Screen</h3>
 *
 * <p>
 *     See the {@link AnalyzeDocumentActivity} for details.
 * </p>
 */
public class AnalyzeDocumentFragmentStandard extends Fragment implements FragmentImplCallback, AnalyzeDocumentFragmentInterface {

    private AnalyzeDocumentFragmentImpl mFragmentImpl;

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment using the provided document.
     * </p>
     * <p>
     *     <b>Note:</b> Always use this method to create new instances. Document is required and an exception is thrown if it's missing.
     * </p>
     * @param document must be the {@link Document} from {@link net.gini.android.vision.reviewdocument.ReviewDocumentFragmentListener#onProceedToAnalyzeScreen(Document)}
     * @return a new instance of the Fragment
     */
    public static AnalyzeDocumentFragmentStandard createInstance(Document document) {
        AnalyzeDocumentFragmentStandard fragment = new AnalyzeDocumentFragmentStandard();
        fragment.setArguments(AnalyzeDocumentFragmentHelper.createArguments(document));
        return fragment;
    }

    /**
     * @exclude
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = AnalyzeDocumentFragmentHelper.createFragmentImpl(this, getArguments());
        AnalyzeDocumentFragmentHelper.setListener(mFragmentImpl, getActivity());
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
    public void startScanAnimation() {
        mFragmentImpl.startScanAnimation();
    }

    @Override
    public void stopScanAnimation() {
        mFragmentImpl.stopScanAnimation();
    }

    @Override
    public void onDocumentAnalyzed() {
        mFragmentImpl.onDocumentAnalyzed();
    }
}
