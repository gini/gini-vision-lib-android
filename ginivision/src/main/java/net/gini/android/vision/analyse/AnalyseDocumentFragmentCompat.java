package net.gini.android.vision.analyse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.ui.FragmentImplCallback;

/**
 * <p>
 *     When using the Component API the {@code AnalyseDocumentFragmentCompat} displays the captured document and an activity indicator while the document is being analysed by the Gini API.
 * </p>
 * <p>
 *     <b>Note:</b> Your Activity hosting this Fragment must extend the {@link android.support.v7.app.AppCompatActivity} and use an AppCompat Theme.
 * </p>
 * <p>
 *     Include the {@code AnalyseDocumentFragmentCompat} into your layout by using the {@link AnalyseDocumentFragmentCompat#createInstance(Document)} factory method to create an instance and display it using the {@link android.support.v4.app.FragmentManager}.
 * </p>
 * <p>
 *     Your Activity must implement the {@link AnalyseDocumentFragmentListener} interface to receive events from the Analyse Document Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link AnalyseDocumentFragmentCompat#onCreate(Bundle)}.
 * </p>
 */
public class AnalyseDocumentFragmentCompat extends Fragment implements FragmentImplCallback, AnalyseDocumentFragmentInterface {

    private AnalyseDocumentFragmentImpl mFragmentImpl;

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment using the provided document.
     * </p>
     * <p>
     *     <b>Note:</b> Always use this method to create new instances. Document is required and an exception is thrown if it's missing.
     * </p>
     * @param document must be the {@link Document} from {@link net.gini.android.vision.reviewdocument.ReviewDocumentFragmentListener#onProceedToAnalyseScreen(Document)}
     * @return a new instance of the Fragment
     */
    public static AnalyseDocumentFragmentCompat createInstance(Document document) {
        AnalyseDocumentFragmentCompat fragment = new AnalyseDocumentFragmentCompat();
        fragment.setArguments(AnalyseDocumentFragmentHelper.createArguments(document));
        return fragment;
    }

    /**
     * @exclude
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = AnalyseDocumentFragmentHelper.createFragmentImpl(this, getArguments());
        AnalyseDocumentFragmentHelper.setListener(mFragmentImpl, getActivity());
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
    public void onDocumentAnalysed() {
        mFragmentImpl.onDocumentAnalysed();
    }
}
