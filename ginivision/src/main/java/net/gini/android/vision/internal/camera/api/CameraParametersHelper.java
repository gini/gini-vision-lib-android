package net.gini.android.vision.internal.camera.api;

import android.hardware.Camera;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
final class CameraParametersHelper {

    private CameraParametersHelper() {
    }

    static boolean isFocusModeSupported(@NonNull final String focusMode, @NonNull final Camera camera) {
        return camera.getParameters().getSupportedFocusModes().contains(focusMode);
    }

    static boolean isUsingFocusMode(@NonNull final String focusMode, @NonNull final Camera camera) {
        return camera.getParameters().getFocusMode().equals(focusMode);
    }

    static boolean isFlashModeSupported(@NonNull final String flashMode, @NonNull final Camera camera) {
        final List<String> supportedFlashModes = camera.getParameters().getSupportedFlashModes();
        return supportedFlashModes != null && supportedFlashModes.contains(flashMode);
    }
}
