package net.gini.android.vision.requirements;

import android.hardware.Camera;

import java.util.List;

import androidx.annotation.NonNull;

class CameraFlashRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    CameraFlashRequirement(final CameraHolder cameraHolder) {
        mCameraHolder = cameraHolder;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.CAMERA_FLASH;
    }

    @NonNull
    @Override
    public RequirementReport check() {
        boolean result = true;
        String details = "";

        try {
            final Camera.Parameters parameters = mCameraHolder.getCameraParameters();
            if (parameters != null) {
                final List<String> supportedFlashModes = parameters.getSupportedFlashModes();
                if (supportedFlashModes == null || !supportedFlashModes.contains(
                        Camera.Parameters.FLASH_MODE_ON)) {
                    result = false;
                    details = "Camera does not support flash";
                }
            } else {
                result = false;
                details = "Camera not open";
            }
        } catch (final RuntimeException e) {
            result = false;
            details = "Camera exception: " + e.getMessage();
        }

        return new RequirementReport(getId(), result, details);
    }
}
