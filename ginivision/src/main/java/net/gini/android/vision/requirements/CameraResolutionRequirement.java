package net.gini.android.vision.requirements;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.camera.api.Util;
import net.gini.android.vision.internal.camera.photo.Size;

class CameraResolutionRequirement implements Requirement {

    // We require ~8MP or higher picture resolutions
    private static final int MIN_PICTURE_AREA = 7900000;

    private final CameraHolder mCameraHolder;

    CameraResolutionRequirement(CameraHolder cameraHolder) {
        mCameraHolder = cameraHolder;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.CAMERA_RESOLUTION;
    }

    @NonNull
    @Override
    public RequirementReport check() {
        boolean result = true;
        String details = "";

        try {
            Camera.Parameters parameters = mCameraHolder.getCameraParameters();
            if (parameters != null) {
                Size previewSize = Util.getLargestFourThreeRatioSize(parameters.getSupportedPreviewSizes());
                if (previewSize == null) {
                    result = false;
                    details = "Camera has no preview resolution with a 4:3 aspect ratio";
                    return new RequirementReport(getId(), result, details);
                }

                Size pictureSize = Util.getLargestFourThreeRatioSize(parameters.getSupportedPictureSizes());
                if (pictureSize == null) {
                    result = false;
                    details = "Camera has no picture resolution with a 4:3 aspect ratio";
                } else if (!isAround8MPOrHigher(pictureSize)) {
                    result = false;
                    details = "Camera picture resolution is lower than 8MP";
                }
            } else {
                result = false;
                details = "Camera not open";
            }
        } catch (RuntimeException e) {
            result = false;
            details = "Camera exception: " + e .getMessage();
        }

        return new RequirementReport(getId(), result, details);
    }

    private boolean isAround8MPOrHigher(Size size) {
        return size.width * size.height >= MIN_PICTURE_AREA;
    }

}
