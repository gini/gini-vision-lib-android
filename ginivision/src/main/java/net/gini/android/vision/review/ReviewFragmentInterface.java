package net.gini.android.vision.review;

import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.camera.Document;

/**
 * <p>
 *     Methods which both Review Document Fragments must implement.
 * </p>
 */
public interface ReviewFragmentInterface {
    /**
     * <p>
     *     You should call this method after you received the analysis results from the Gini API.
     * </p>
     * <p>
     *     This is important for managing the behaviour of the Review Document Fragment when the Next button was clicked.
     * </p>
     * <p>
     *     If the document was already analyzed and the image wasn't changed when the user tapped the Next button, {@link ReviewFragmentListener#onDocumentReviewedAndAnalyzed(Document)} is called and there is no need to show an {@link AnalysisActivity} or {@link AnalysisFragmentStandard} or {@link AnalysisFragmentCompat}.
     * </p>
     * <p>
     *     If the document wasn't analyzed or the image was changed when the user tapped the Next button, {@link ReviewFragmentListener#onProceedToAnalyzeScreen(Document)} is called.
     * </p>
     */
    void onDocumentAnalyzed();
}
