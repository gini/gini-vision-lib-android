package net.gini.android.vision.internal.camera.api;

/**
 * Exception that is thrown when there is some issue with the camera (i.e. no connection possible or device doesn't
 * even have a camera).
 *
 * @exclude
 */
public class CameraException extends RuntimeException {

    public CameraException(final String detailMessage) {
        super(detailMessage);
    }
}
