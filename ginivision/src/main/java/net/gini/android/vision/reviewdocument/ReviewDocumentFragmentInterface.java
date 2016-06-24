package net.gini.android.vision.reviewdocument;

import net.gini.android.vision.scanner.Document;

/**
 * <p>
 *     Methods which both Review Document Fragment's must implement.
 * </p>
 */
public interface ReviewDocumentFragmentInterface {
    /**
     * <p>
     *     You should call this method after you received the analysis results from the Gini API.
     * </p>
     * <p>
     *     This is important for managing the behaviour of the Review Document Fragment when the Next button was clicked.
     * </p>
     * <p>
     *     If the document was already analysed and the image wasn't changed when the user tapped the Next button, {@link ReviewDocumentFragmentListener#onDocumentReviewedAndAnalysed(Document)} is called and there is no need to show an {@link net.gini.android.vision.analyse.AnalyseDocumentActivity} or {@link net.gini.android.vision.analyse.AnalyseDocumentFragmentStandard} or {@link net.gini.android.vision.analyse.AnalyseDocumentFragmentCompat}.
     * </p>
     * <p>
     *     If the document wasn't analysed or the image was changed when the user tapped the Next button, {@link ReviewDocumentFragmentListener#onProceedToAnalyseScreen(Document)} is called.
     * </p>
     */
    void onDocumentAnalysed();
}
