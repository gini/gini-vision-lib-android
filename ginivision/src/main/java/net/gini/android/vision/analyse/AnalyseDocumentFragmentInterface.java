package net.gini.android.vision.analyse;

/**
 * <p>
 *     Methods which both Analyse Document Fragment's must implement.
 * </p>
 */
public interface AnalyseDocumentFragmentInterface {
    /**
     * <p>
     *     You should call this method when you start the document analysis using the Gini API.
     * </p>
     */
    void startScanAnimation();

    /**
     * <p>
     *     You should call this method when the document analysis finished.
     * </p>
     */
    void stopScanAnimation();

    /**
     * <p>
     *     You should call this method after you received the analysis results from the Gini API.
     * </p>
     */
    void onDocumentAnalysed();
}
