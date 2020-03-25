package net.gini.android.vision.analysis;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.Document;
import net.gini.android.vision.internal.ui.FragmentImplCallback;
import net.gini.android.vision.internal.util.AlertDialogHelperStandard;
import net.gini.android.vision.review.ReviewFragmentListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <h3>Component API</h3>
 *
 * <p>
 *     When you use the Component API without the Android Support Library, the {@code AnalyzeDocumentFragmentStandard} displays the captured or imported document and an activity indicator while the document is being analyzed by the Gini API.
 * </p>
 * <p>
 *     <b>Note:</b> You can use the activity indicator message to display a message under the activity indicator by overriding the string resource named {@code gv_analysis_activity_indicator_message}. The message is displayed for images only.
 * </p>
 * <p>
 *     For PDF documents the first page is shown (only on Android 5.0 Lollipop and newer) along with the PDF's filename and number of pages above the page. On Android KitKat and older only the PDF's filename is shown with the preview area left empty.
 * </p>
 * <p>
 *     Include the {@code AnalyzeDocumentFragmentStandard} into your layout by using the {@link AnalysisFragmentStandard#createInstance(Document, String)} factory method to create an instance and display it using the {@link android.app.FragmentManager}.
 * </p>
 * <p>
 *     An {@link AnalysisFragmentListener} instance must be available until the {@code AnalysisFragmentStandard} is attached to an activity. Failing to do so will throw an exception.
 *     The listener instance can be provided either implicitly by making the hosting Activity implement the {@link AnalysisFragmentListener} interface or explicitly by
 *     setting the listener using {@link AnalysisFragmentCompat#setListener(AnalysisFragmentListener)}.
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
public class AnalysisFragmentStandard extends Fragment implements FragmentImplCallback,
        AnalysisFragmentInterface {

    private AnalysisFragmentImpl mFragmentImpl;
    private AnalysisFragmentListener mListener;

    @Override
    public void hideError() {
        mFragmentImpl.hideError();
    }

    @Override
    public void onNoExtractionsFound() {
        mFragmentImpl.onNoExtractionsFound();
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentImpl = AnalysisFragmentHelper.createFragmentImpl(this, getArguments());
        AnalysisFragmentHelper.setListener(mFragmentImpl, getActivity(), mListener);
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
    public void onDestroy() {
        super.onDestroy();
        mFragmentImpl.onDestroy();
    }

    @Override
    public void onDocumentAnalyzed() {
        mFragmentImpl.onDocumentAnalyzed();
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
    public void showError(@NonNull final String message, @NonNull final String buttonTitle,
            @NonNull final View.OnClickListener onClickListener) {
        mFragmentImpl.showError(message, buttonTitle, onClickListener);
    }

    @Override
    public void showError(@NonNull final String message, final int duration) {
        mFragmentImpl.showError(message, duration);
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
    public void setListener(@NonNull final AnalysisFragmentListener listener) {
        if (mFragmentImpl != null) {
            mFragmentImpl.setListener(listener);
        }
        mListener = listener;
    }

    /**
     * <p>
     * Factory method for creating a new instance of the Fragment using the provided document.
     * </p>
     * <p>
     * You may pass in an optional analysis error message. This error message is shown to the user
     * with a retry
     * button.
     * </p>
     * <p>
     * <b>Note:</b> Always use this method to create new instances. Document is required and an
     * exception is thrown if it's missing.
     * </p>
     *
     * @param document                     must be the {@link Document} from {@link
     *                                     ReviewFragmentListener#onProceedToAnalysisScreen
     *                                     (Document)}
     * @param documentAnalysisErrorMessage an optional error message shown to the user
     * @return a new instance of the Fragment
     */
    public static AnalysisFragmentStandard createInstance(@NonNull final Document document,
            @Nullable final String documentAnalysisErrorMessage) {
        final AnalysisFragmentStandard fragment = new AnalysisFragmentStandard();
        fragment.setArguments(
                AnalysisFragmentHelper.createArguments(document, documentAnalysisErrorMessage));
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
        AlertDialogHelperStandard.showAlertDialog(activity, message, positiveButtonTitle,
                positiveButtonClickListener, negativeButtonTitle, negativeButtonClickListener,
                cancelListener);
    }

}
