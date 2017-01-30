package net.gini.android.vision.internal.camera.api;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * @exclude
 */
final class CameraParametersHelper {

    private CameraParametersHelper() {
    }

    static boolean isFocusModeSupported(@NonNull String focusMode, @NonNull Camera camera) {
        return camera.getParameters().getSupportedFocusModes().contains(focusMode);
    }

    static boolean isUsingFocusMode(@NonNull String focusMode, @NonNull Camera camera) {
        return camera.getParameters().getFocusMode().equals(focusMode);
    }

    static boolean isFlashModeSupported(@NonNull String flashMode, @NonNull Camera camera) {
        List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
        return supportedFlashModes != null && supportedFlashModes.contains(flashMode);
    }
}
