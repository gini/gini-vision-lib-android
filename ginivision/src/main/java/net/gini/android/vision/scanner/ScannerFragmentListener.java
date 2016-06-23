package net.gini.android.vision.scanner;

import net.gini.android.vision.GiniVisionError;

/**
 * <p>
 * Interface used by {@link ScannerFragmentStandard} and {@link ScannerFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface ScannerFragmentListener {
    /**
     * <p>
     * Called when the user took an image with the camera.
     * </p>
     *
     * @param document the image taken by the camera
     */
    void onDocumentAvailable(Document document);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(GiniVisionError error);
}
