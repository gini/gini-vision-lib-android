package net.gini.android.vision.review;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisActivity;

/**
 * <p>
 * Interface used by {@link ReviewFragmentStandard} and {@link ReviewFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface ReviewFragmentListener {
    /**
     * <p>
     * Called when the Review Fragment was started and you should start analyzing the original document by sending it to the Gini API.
     * </p>
     * <p>
     *     We assume that in most cases the photo is good enough and this way we are able to provide analysis results quicker.
     * </p>
     * <p>
     *     <b>Note:</b> Call {@link ReviewFragmentStandard#onDocumentAnalyzed()} or {@link ReviewFragmentCompat#onDocumentAnalyzed()} when the analysis is done and your Activity wasn't stopped.
     * </p>
     * @param document contains the original image taken by the camera
     */
    void onShouldAnalyzeDocument(@NonNull Document document);

    /**
     * <p>
     *     Called if you didn't call {@link ReviewFragmentStandard#onDocumentAnalyzed()} or {@link ReviewFragmentCompat#onDocumentAnalyzed()} or the image was changed and the user tapped on the Next button.
     * </p>
     * <p>
     *     You should start your Activity extending {@link AnalysisActivity} and set the document as the {@link AnalysisActivity#EXTRA_IN_DOCUMENT} extra.
     * </p>
     *
     * @param document contains the reviewed image (can be the original one or a modified image)
     */
    void onProceedToAnalysisScreen(@NonNull Document document);

    /**
     * <p>
     *     Called if you called {@link ReviewFragmentStandard#onDocumentAnalyzed()} or {@link ReviewFragmentCompat#onDocumentAnalyzed()} and the image wasn't changed and the user tapped on the Next button.
     * </p>
     * <p>
     *     You should finish your Activity and proceed to handling the results of the analysis.
     * </p>
     * @param document contains the reviewed image (can be the original one or a modified image)
     */
    void onDocumentReviewedAndAnalyzed(@NonNull Document document);

    /**
     * <p>
     *      Called when the user rotated the image.
     * </p>
     * <p>
     *     In case you started the document analysis in {@link ReviewFragmentListener#onShouldAnalyzeDocument(Document)}
     *     you should cancel it here as the original image is not valid anymore.
     * </p>
     *
     * @param document contains the modified image
     * @param oldRotation the previous rotation in degrees
     * @param newRotation the new rotation in degrees
     */
    void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(@NonNull GiniVisionError error);
}
