package net.gini.android.vision.camera;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;

/**
 * <p>
 * Interface used by {@link CameraFragmentStandard} and {@link CameraFragmentCompat} to dispatch events to the hosting Activity.
 * </p>
 */
public interface CameraFragmentListener {
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
