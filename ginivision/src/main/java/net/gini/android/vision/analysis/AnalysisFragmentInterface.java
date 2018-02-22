package net.gini.android.vision.analysis;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * <p>
 * Methods which both Analysis Fragments must implement.
 * </p>
 */
public interface AnalysisFragmentInterface {
    /**
     * <p>
     * Call this method to hide the error shown before with
     * {@link AnalysisFragmentInterface#showError(String, String, View.OnClickListener)} or
     * {@link AnalysisFragmentInterface#showError(String, int)}.
     * </p>
     */
    void hideError();

    /**
     * <p>
     * You should call this method after you've received the analysis results from the Gini API
     * without the required extractions.
     * </p>
     */
    void onNoExtractionsFound();

    /**
     * <p>
     * You should call this method after you've received the analysis results from the Gini API.
     * </p>
     */
    void onDocumentAnalyzed();

    /**
     * <p>
     * Call this method when you need to show an error message to the user in the Analysis
     * Screen.
     * </p>
     *
     * @param message  a short error message
     * @param duration how long should the error message be shown in ms
     */
    void showError(@NonNull String message, int duration);

    /**
     * <p>
     * Call this method when you need to show an error message with an invokable action to the user
     * in the Analysis Screen.
     * </p>
     *
     * @param message         a short error message
     * @param buttonTitle     if not null and not empty, shows a button with the given title
     * @param onClickListener listener for the button
     */
    void showError(@NonNull String message, @NonNull String buttonTitle,
            @NonNull View.OnClickListener onClickListener);

    /**
     * <p>
     * You should call this method when you start the document analysis using the Gini API.
     * </p>
     */
    void startScanAnimation();

    /**
     * <p>
     * You should call this method when the document analysis has finished.
     * </p>
     */
    void stopScanAnimation();

    /**
     * <p>
     *     Set a listener for analysis events.
     * </p>
     * <p>
     *     By default the hosting Activity is expected to implement
     *     the {@link AnalysisFragmentListener}. In case that is not feasible you may set the
     *     listener using this method.
     * </p>
     * <p>
     *     <b>Note:</b> the listener is expected to be available until the fragment is
     *     attached to an activity. Make sure to set the listener before that.
     * </p>
     * @param listener {@link AnalysisFragmentListener} instance
     */
    void setListener(@NonNull final AnalysisFragmentListener listener);
}
