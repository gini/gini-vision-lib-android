package net.gini.android.vision.requirements;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import java.util.List;

class CameraFlashRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    CameraFlashRequirement(CameraHolder cameraHolder) {
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

        Camera camera = mCameraHolder.getCamera();
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();
            List<String> supportedFlashModes = parameters.getSupportedFlashModes();
            if (supportedFlashModes == null || !supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                result = false;
                details = "Camera does not support flash";
            }
        } else {
            result = false;
            details = "Camera not open";
        }

        return new RequirementReport(getId(), result, details);
    }
}
