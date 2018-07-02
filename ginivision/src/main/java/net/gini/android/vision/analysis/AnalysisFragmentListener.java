package net.gini.android.vision.analysis;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.noresults.NoResultsFragmentCompat;
import net.gini.android.vision.noresults.NoResultsFragmentStandard;

import java.util.Map;

/**
 * <p>
 * Interface used by {@link AnalysisFragmentStandard} and {@link AnalysisFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface AnalysisFragmentListener {
    /**
     * <p>
     *     Called when the Analyze Document Fragment is started and the document can be analyzed.
     * </p>
     * @param document contains the image taken by the camera (original or modified)
     *
     * @deprecated When a {@link GiniVision} instance is available the document
     * is analyzed internally by using the configured {@link GiniVisionNetworkService}
     * implementation. The extractions will be returned in {@link AnalysisFragmentListener#onExtractionsAvailable(Map)}.
     */
    @Deprecated
    void onAnalyzeDocument(@NonNull Document document);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(@NonNull GiniVisionError error);

    /**
     * Called when the document has been analyzed and extractions are available.
     *
     * @param extractions a map of the extractions with the extraction labels as keys
     */
    void onExtractionsAvailable(
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions);

    /**
     * Called when the document has been analyzed and no extractions were received.
     * <p>
     * You should show the {@link NoResultsFragmentStandard} or {@link NoResultsFragmentCompat}.
     *
     * @param document contains the reviewed document
     */
    void onProceedToNoExtractionsScreen(@NonNull final Document document);
}
