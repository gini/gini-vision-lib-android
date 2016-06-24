package net.gini.android.vision.reviewdocument;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.Document;

/**
 * <p>
 * Interface used by {@link ReviewDocumentFragmentStandard} and {@link ReviewDocumentFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface ReviewDocumentFragmentListener {
    /**
     * <p>
     * Called when the Review Document Fragment was started and you should start analysing the original document by sending it to the Gini API.
     * </p>
     * <p>
     *     We assume that in most cases the photo is good enough and this way we are able to provide analysis results quicker.
     * </p>
     * <p>
     *     <b>Note:</b> Call {@link ReviewDocumentFragmentStandard#onDocumentAnalyzed()} or {@link ReviewDocumentFragmentCompat#onDocumentAnalyzed()} when the analysis is done and your Activity wasn't stopped.
     * </p>
     * @param document contains the original image taken by the camera
     */
    void onShouldAnalyzeDocument(Document document);

    /**
     * <p>
     *     Called when you didn't call {@link ReviewDocumentFragmentStandard#onDocumentAnalyzed()} or {@link ReviewDocumentFragmentCompat#onDocumentAnalyzed()} or the image was changed and the user tapped on the Next button.
     * </p>
     * <p>
     *     You should start your Activity extending {@link AnalysisActivity} and set the document as the {@link AnalysisActivity#EXTRA_IN_DOCUMENT} extra.
     * </p>
     *
     * @param document contains the reviewed image (can be the original one or a modified image)
     */
    void onProceedToAnalyzeScreen(Document document);

    /**
     * <p>
     *     Called when you called {@link ReviewDocumentFragmentStandard#onDocumentAnalyzed()} or {@link ReviewDocumentFragmentCompat#onDocumentAnalyzed()} and the image wasn't changed and the user tapped on the Next button.
     * </p>
     * <p>
     *     You should finish your Activity and proceed to handling the results of the analysis.
     * </p>
     * @param document contains the reviewed image (can be the original one or a modified image)
     */
    void onDocumentReviewedAndAnalyzed(Document document);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(GiniVisionError error);
}
