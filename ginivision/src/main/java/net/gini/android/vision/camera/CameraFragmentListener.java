package net.gini.android.vision.camera;

import android.support.annotation.NonNull;

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
     * Called when the user has taken an image with the camera.
     * </p>
     *
     * @param document the image taken by the camera
     */
    void onDocumentAvailable(@NonNull Document document);

    void onCheckImportedDocument(@NonNull Document document,
            @NonNull DocumentCheckResultCallback callback);

    /**
     * <p>
     * Called when an error occurred.
     * </p>
     * @param error details about what went wrong
     */
    void onError(@NonNull GiniVisionError error);

    interface DocumentCheckResultCallback {
        void documentAccepted();
        void documentRejected(@NonNull String messageForUser);
    }
}
