package net.gini.android.vision.requirements;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import java.util.List;

class CameraAutoFocusRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    CameraAutoFocusRequirement(CameraHolder cameraHolder) {
        mCameraHolder = cameraHolder;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.CAMERA_AUTOFOCUS;
    }

    @NonNull
    @Override
    public RequirementReport check() {
        boolean fulfilled = true;
        String details = "";

        Camera camera = mCameraHolder.getCamera();
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes == null || !supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                fulfilled = false;
                details = "Camera does not support auto-focus";
            }
        } else {
            fulfilled = false;
            details = "Camera not open";
        }

        return new RequirementReport(getId(), fulfilled, details);
    }
}
