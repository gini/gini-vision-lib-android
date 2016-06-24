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
     *     If the document was already analyzed and the image wasn't changed when the user tapped the Next button, {@link ReviewDocumentFragmentListener#onDocumentReviewedAndAnalyzed(Document)} is called and there is no need to show an {@link net.gini.android.vision.analyze.AnalyzeDocumentActivity} or {@link net.gini.android.vision.analyze.AnalyzeDocumentFragmentStandard} or {@link net.gini.android.vision.analyze.AnalyzeDocumentFragmentCompat}.
     * </p>
     * <p>
     *     If the document wasn't analyzed or the image was changed when the user tapped the Next button, {@link ReviewDocumentFragmentListener#onProceedToAnalyzeScreen(Document)} is called.
     * </p>
     */
    void onDocumentAnalyzed();
}
