package net.gini.android.vision.internal.camera.api;

import android.hardware.Camera;

import java.util.List;

/**
 * @exclude
 */
final class CameraParametersHelper {

    private CameraParametersHelper() {
    }

    static boolean isFocusModeSupported(String focusMode, Camera camera) {
        return camera.getParameters().getSupportedFocusModes().contains(focusMode);
    }

    static boolean isUsingFocusMode(String focusMode, Camera camera) {
        return camera.getParameters().getFocusMode().equals(focusMode);
    }

    static boolean isFlashModeSupported(String flashMode, Camera camera) {
        List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
        return supportedFlashModes != null && supportedFlashModes.contains(flashMode);
    }
}
