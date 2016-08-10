package net.gini.android.vision.requirements;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import java.util.List;

class CameraFocusRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    CameraFocusRequirement(CameraHolder cameraHolder) {
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
            Camera.Parameters parameters = mCameraHolder.getCameraParameters();
            if (parameters != null) {
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                if (supportedFocusModes == null || !supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)
                        || !supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    fulfilled = false;
                    details = "Camera does not support auto-focus";
                }
            } else {
                fulfilled = false;
                details = "Camera not open";
            }
        } catch (RuntimeException e) {
            fulfilled = false;
            details = "Camera exception: " + e.getMessage();
        }

        return new RequirementReport(getId(), fulfilled, details);
    }
}
