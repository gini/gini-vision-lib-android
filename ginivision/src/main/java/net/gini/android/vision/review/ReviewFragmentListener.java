package net.gini.android.vision.review;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.noresults.NoResultsFragmentCompat;
import net.gini.android.vision.noresults.NoResultsFragmentStandard;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Interface used by {@link ReviewFragmentStandard} and {@link ReviewFragmentCompat} to dispatch
 * events to the hosting Activity.
 */
public interface ReviewFragmentListener {

    /**
     * Called when the Review Fragment was started and you should start analyzing the original
     * document by sending it to the Gini API.
     *
     * <p> We assume that in most cases the photo is good enough and this way we are able to provide
     * analysis results quicker.
     *
     * <p> <b>Note:</b> Call {@link ReviewFragmentStandard#onDocumentAnalyzed()} or {@link
     * ReviewFragmentCompat#onDocumentAnalyzed()} when the analysis is done and your Activity wasn't
     * stopped.
     *
     * @param document contains the original image taken by the camera
     *
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed
     * internally by using the configured {@link GiniVisionNetworkService} implementation. The
     * extractions will be returned in the Analysis Screen in
     * {@link AnalysisFragmentListener#onExtractionsAvailable(Map, Map)}.
     */
    @Deprecated
    void onShouldAnalyzeDocument(@NonNull Document document);

    /**
     * Called if you didn't call {@link ReviewFragmentStandard#onDocumentAnalyzed()} or {@link
     * ReviewFragmentCompat#onDocumentAnalyzed()} (or the image was changed) and the user tapped on
     * the Next button.
     *
     * <p> You should start your Activity extending {@link AnalysisActivity} and set the document as
     * the {@link AnalysisActivity#EXTRA_IN_DOCUMENT} extra.
     *
     * @param document contains the reviewed image (can be the original one or a modified image)
     *
     * @Deprecated When a {@link GiniVision} instance is available {@link ReviewFragmentListener#onProceedToAnalysisScreen(Document,
     * String)} is invoked instead.
     */
    @Deprecated
    void onProceedToAnalysisScreen(@NonNull Document document);

    /**
     * Called if you called {@link ReviewFragmentStandard#onDocumentAnalyzed()} or {@link
     * ReviewFragmentCompat#onDocumentAnalyzed()} and the image wasn't changed and the user tapped
     * on the Next button.
     *
     * <p> You should finish your Activity and proceed to handling the results of the analysis.
     *
     * @param document contains the reviewed image (can be the original one or a modified image)
     *
     * @Deprecated When a {@link GiniVision} instance is available the document is analyzed
     * internally by using the configured {@link GiniVisionNetworkService} implementation. The
     * extractions will be returned in the Analysis Screen in
     * {@link AnalysisFragmentListener#onExtractionsAvailable(Map, Map)}.
     */
    @Deprecated
    void onDocumentReviewedAndAnalyzed(@NonNull Document document);

    /**
     * <p> Called when the user rotated the image.
     *
     * <p> In case you started the document analysis in {@link ReviewFragmentListener#onShouldAnalyzeDocument(Document)}
     * you should cancel it here as the original image is not valid anymore.
     *
     * @param document    contains the modified image
     * @param oldRotation the previous rotation in degrees
     * @param newRotation the new rotation in degrees
     *
     * @Deprecated When a {@link GiniVision} and a {@link GiniVisionNetworkService} instance is
     * available rotation is handled internally. The document is analyzed by using the configured
     * {@link GiniVisionNetworkService} implementation. The extractions will be returned in the
     * Analysis Screen in {@link AnalysisFragmentListener#onExtractionsAvailable(Map, Map)}.
     */
    @Deprecated
    void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation);

    /**
     * Called when an error occurred.
     *
     * @param error details about what went wrong
     */
    void onError(@NonNull GiniVisionError error);

    /**
     * Called when the document has been analyzed and extractions are available.
     *
     * @param extractions a map of the extractions with the extraction labels as keys
     *
     * @deprecated When a {@link GiniVision} instance is available the document is analyzed
     * internally by using the configured {@link GiniVisionNetworkService} implementation. The
     * extractions will be returned in the Analysis Screen in
     * {@link AnalysisFragmentListener#onExtractionsAvailable(Map, Map)}.
     */
    @Deprecated
    void onExtractionsAvailable(
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions);

    /**
     * Called when the document has been analyzed and no extractions were received.
     *
     * <p> You should show the {@link NoResultsFragmentStandard} or {@link
     * NoResultsFragmentCompat}.
     *
     * @param document contains the reviewed document
     *
     * @deprecated When a {@link GiniVision} instance is available the document is analyzed
     * internally by using the configured {@link GiniVisionNetworkService} implementation. The
     * extractions will be returned in the Analysis Screen in
     * {@link AnalysisFragmentListener#onExtractionsAvailable(Map, Map)}.
     */
    @Deprecated
    void onProceedToNoExtractionsScreen(@NonNull final Document document);

    /**
     * Called when the user tapped on the Next button and one of the following conditions apply:
     * <ul> <li>Analysis is in progress <li>Analysis completed with an error <li>The image was
     * rotated
     *
     * <p> You should start your Activity extending {@link AnalysisActivity} and set the document as
     * the {@link AnalysisActivity#EXTRA_IN_DOCUMENT} extra.
     *
     * @param document     contains the reviewed image (can be the original one or a modified
     *                     image)
     * @param errorMessage an optional error message to be passed to the Analysis Screen
     */
    void onProceedToAnalysisScreen(@NonNull Document document, @Nullable String errorMessage);
}
