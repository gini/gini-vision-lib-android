package net.gini.android.vision.review;

import net.gini.android.vision.Document;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;

/**
 * <p>
 *     Methods which both Review Fragments must implement.
 * </p>
 */
public interface ReviewFragmentInterface {
    /**
     * <p>
     *     You should call this method after you've received the analysis results from the Gini API.
     * </p>
     * <p>
     *     This is important for managing the behavior of the Review Document Fragment when the Next button was clicked.
     * </p>
     * <p>
     *     If the document has already been analyzed and the image wasn't changed when the user tapped the Next button, {@link ReviewFragmentListener#onDocumentReviewedAndAnalyzed(Document)} is called and there is no need to show an {@link AnalysisActivity} or {@link AnalysisFragmentStandard} or {@link AnalysisFragmentCompat}.
     * </p>
     * <p>
     *     If the document wasn't analyzed or the image was changed when the user tapped the Next button, {@link ReviewFragmentListener#onProceedToAnalysisScreen(Document)} is called.
     * </p>
     */
    void onDocumentAnalyzed();

    /**
     * <p>
     * You should call this method after you've received the analysis results from the Gini API
     * without the required extractions.
     * </p>
     */
    void onNoExtractionsFound();
}
