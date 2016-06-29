package net.gini.android.vision.analysis;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.Document;

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
    void onAnalyzeDocument(Document document);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(GiniVisionError error);
}
