package net.gini.android.vision.requirements;

import androidx.annotation.NonNull;

class CameraRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    CameraRequirement(final CameraHolder cameraHolder) {
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
            if (!mCameraHolder.hasCamera()) {
                result = false;
                details = "No back-facing camera found";
            }
        } catch (final RuntimeException e) {
            result = false;
            details = "Camera could not be opened: " + e.getMessage();
        }
        return new RequirementReport(getId(), result, details);
    }
}
