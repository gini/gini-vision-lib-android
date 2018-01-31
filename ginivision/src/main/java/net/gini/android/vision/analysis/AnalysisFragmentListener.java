package net.gini.android.vision.analysis;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

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
     */
    // WIP: called only if not using GiniVisionApplication
    void onAnalyzeDocument(@NonNull Document document);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(@NonNull GiniVisionError error);

    // WIP: analysis screen analyse document
    void onExtractionsAvailable(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions);

    // WIP: analysis screen analyse document
    void onProceedToNoExtractionsScreen(@NonNull final Document document);
}
