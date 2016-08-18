package net.gini.android.vision.analysis;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.Document;
import net.gini.android.vision.review.ReviewFragmentListener;
import net.gini.android.vision.ui.FragmentImplCallback;

/**
 * <h3>Component API</h3>
 *
 * <p>
 *     When you use the Component API without the Android Support Library, the {@code AnalyzeDocumentFragmentStandard} displays the captured document and an activity indicator while the document is being analyzed by the Gini API.
 * </p>
 * <p>
 *     Include the {@code AnalyzeDocumentFragmentStandard} into your layout by using the {@link AnalysisFragmentStandard#createInstance(Document, String)} factory method to create an instance and display it using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 *     Your Activity must implement the {@link AnalysisFragmentListener} interface to receive events from the Analyze Document Fragment. Failing to do so will throw an exception.
 * </p>
 * <p>
 *     Your Activity is automatically set as the listener in {@link AnalysisFragmentStandard#onCreate(Bundle)}.
 * </p>
 *
 * <h3>Customizing the Analysis Screen</h3>
 *
 * <p>
 *     See the {@link AnalysisActivity} for details.
 * </p>
 */
public class AnalysisFragmentStandard extends Fragment implements FragmentImplCallback, AnalysisFragmentInterface {

    private AnalysisFragmentImpl mFragmentImpl;

    /**
     * <p>
     *     Factory method for creating a new instance of the Fragment using the provided document.
     * </p>
     * <p>
     *     You may pass in an optional analysis error message. This error message is shown to the user with a retry
     *     button.
     * </p>
     * <p>
     *     <b>Note:</b> Always use this method to create new instances. Document is required and an exception is thrown if it's missing.
     * </p>
     * @param document must be the {@link Document} from {@link ReviewFragmentListener#onProceedToAnalysisScreen(Document)}
     * @param documentAnalysisErrorMessage an optional error message shown to the user
     * @return a new instance of the Fragment
     */
    public static AnalysisFragmentStandard createInstance(@NonNull Document document, @Nullable String documentAnalysisErrorMessage) {
        AnalysisFragmentStandard fragment = new AnalysisFragmentStandard();
        fragment.setArguments(AnalysisFragmentHelper.createArguments(document, documentAnalysisErrorMessage));
        return fragment;
    }

    /**
     * @exclude
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = AnalysisFragmentHelper.createFragmentImpl(this, getArguments());
        AnalysisFragmentHelper.setListener(mFragmentImpl, getActivity());
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
     * @exclude
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

    @Override
    public void showError(@NonNull String message, @NonNull String buttonTitle, @NonNull View.OnClickListener onClickListener) {
        mFragmentImpl.showError(message, buttonTitle, onClickListener);
    }

    @Override
    public void showError(@NonNull String message, int duration) {
        mFragmentImpl.showError(message, duration);
    }

    @Override
    public void hideError() {
        mFragmentImpl.hideError();
    }
}
