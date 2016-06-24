package net.gini.android.vision.analyse;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.Document;

/**
 * <p>
 * Interface used by {@link AnalyseDocumentFragmentStandard} and {@link AnalyseDocumentFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface AnalyseDocumentFragmentListener {
    /**
     * <p>
     *     Called when the Analyse Document Fragment started and the document can be analysed.
     * </p>
     * @param document contains the image taken by the camera (original or modified)
     */
    void onAnalyseDocument(Document document);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(GiniVisionError error);
}
