package net.gini.android.vision.camera;

import net.gini.android.vision.GiniVisionError;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
final class Util {

    private static final String CAMERA_EXCEPTION_MESSAGE_NO_ACCESS =
            "Fail to connect to camera service";

    @NonNull
    static GiniVisionError cameraExceptionToGiniVisionError(@NonNull final Exception exception) {
        // String comparison is the only way to determine the cause of the camera exception with the old Camera API
        // Here are the possible error messages:
        // https://android.googlesource.com/platform/frameworks/base/+/marshmallow-release/core/java/android/hardware/Camera.java#415
        final String message = exception.getMessage();
        if (message.equals(CAMERA_EXCEPTION_MESSAGE_NO_ACCESS)) {
            return new GiniVisionError(GiniVisionError.ErrorCode.CAMERA_NO_ACCESS, message);
        } else {
            return new GiniVisionError(GiniVisionError.ErrorCode.CAMERA_UNKNOWN, message);
        }
    }

    private Util() {
    }
}
