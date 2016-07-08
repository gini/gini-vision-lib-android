package net.gini.android.vision.camera;

import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionError;

class Util {

    @NonNull
    public static GiniVisionError cameraExceptionToGiniVisionError(@NonNull RuntimeException exception) {
        // String comparison is the only way to determine the cause of the camera exception with the old Camera API
        // Here are the possible error messages:
        // https://android.googlesource.com/platform/frameworks/base/+/marshmallow-release/core/java/android/hardware/Camera.java#415
        String message = exception.getMessage();
        if (message.equals("Fail to connect to camera service")) {
            return new GiniVisionError(GiniVisionError.ErrorCode.CAMERA_NO_ACCESS, message);
        } else {
            return new GiniVisionError(GiniVisionError.ErrorCode.CAMERA_UNKNOWN, message);
        }
    }

    private Util() {
    }
}
