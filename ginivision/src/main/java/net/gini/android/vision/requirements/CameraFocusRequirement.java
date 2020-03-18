package net.gini.android.vision.requirements;

import android.hardware.Camera;

import java.util.List;

import androidx.annotation.NonNull;

class CameraFocusRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    CameraFocusRequirement(final CameraHolder cameraHolder) {
        mCameraHolder = cameraHolder;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.CAMERA_FOCUS;
    }

    @NonNull
    @Override
    public RequirementReport check() {
        boolean fulfilled = true;
        String details = "";

        try {
            final Camera.Parameters parameters = mCameraHolder.getCameraParameters();
            if (parameters != null) {
                final List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes == null || !supportedFocusModes.contains(
                        Camera.Parameters.FOCUS_MODE_AUTO)) {
                    fulfilled = false;
                    details = "Camera does not support auto-focus";
                }
            } else {
                fulfilled = false;
                details = "Camera not open";
            }
        } catch (final RuntimeException e) {
            fulfilled = false;
            details = "Camera exception: " + e.getMessage();
        }

        return new RequirementReport(getId(), fulfilled, details);
    }
}
