package net.gini.android.vision.requirements;

import android.support.annotation.NonNull;

class CameraRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    CameraRequirement(CameraHolder cameraHolder) {
        mCameraHolder = cameraHolder;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.CAMERA;
    }

    @NonNull
    @Override
    public RequirementReport check() {
        boolean result = true;
        String details = "";
        try {
            // Camera must be closed by the creator of the camera holder
            mCameraHolder.openCamera();
            if (mCameraHolder.getCamera() == null) {
                result = false;
                details = "No back-facing camera found";
            }
        } catch (RuntimeException e) {
            result = false;
            details = "Camera could not be opened: " + e.getMessage();
        }
        return new RequirementReport(getId(), result, details);
    }
}
