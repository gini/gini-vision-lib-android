package net.gini.android.vision.requirements;

import android.hardware.Camera;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.camera.api.SizeSelectionHelper;
import net.gini.android.vision.internal.util.Size;

import java.util.Locale;

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
                Size pictureSize = SizeSelectionHelper.getLargestSize(parameters.getSupportedPictureSizes());
                if (pictureSize == null) {
                    result = false;
                    details = "Camera has no picture resolutions";
                    return new RequirementReport(getId(), result, details);
                } else if (!isAround8MPOrHigher(pictureSize)) {
                    result = false;
                    details = "Largest camera picture resolution is lower than 8MP";
                    return new RequirementReport(getId(), result, details);
                }

                Size previewSize = SizeSelectionHelper.getLargestSizeWithSimilarAspectRatio(
                        parameters.getSupportedPreviewSizes(), pictureSize);
                if (previewSize == null) {
                    result = false;
                    details = String.format(Locale.US,
                            "Camera has no preview resolutions matching the picture resolution "
                                    + "%dx%d",
                            pictureSize.width, pictureSize.height);
                    return new RequirementReport(getId(), result, details);
                }
            } else {
                result = false;
                details = "Camera not open";
            }
        } catch (RuntimeException e) {
            result = false;
            details = "Camera exception: " + e.getMessage();
        }

        return new RequirementReport(getId(), result, details);
    }

    private boolean isAround8MPOrHigher(Size size) {
        return size.width * size.height >= MIN_PICTURE_AREA;
    }

}
