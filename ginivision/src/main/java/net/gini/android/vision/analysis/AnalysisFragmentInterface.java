package net.gini.android.vision.analysis;

import android.view.View;

/**
 * <p>
 *     Methods which both Analyze Document Fragments must implement.
 * </p>
 */
public interface AnalysisFragmentInterface {
    /**
     * <p>
     *     You should call this method when you start the document analysis using the Gini API.
     * </p>
     */
    void startScanAnimation();

    /**
     * <p>
     *     You should call this method when the document analysis finished.
     * </p>
     */
    void stopScanAnimation();

    /**
     * <p>
     *     You should call this method after you received the analysis results from the Gini API.
     * </p>
     */
    void onDocumentAnalyzed();

    /**
     * <p>
     *     Call this method when you need to show a message to the user in the Analysis Screen.
     * </p>
     * <p>
     *     If you wish to provide an invokable action to the user, you can set a button title and an {@link android.view.View.OnClickListener}.
     * </p>
     * @param message a short error message
     * @param buttonTitle if not null and not empty, shows a button with the given title
     * @param onClickListener listener for the button
     * @param duration how long should the error message be shown in ms
     */
    void showError(String message, String buttonTitle, View.OnClickListener onClickListener, int duration);
}
