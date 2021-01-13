package net.gini.android.vision.requirements;

import android.hardware.Camera;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import net.gini.android.vision.internal.camera.api.SizeSelectionHelper;
import net.gini.android.vision.internal.util.Size;

/**
 * Internal use only.
 *
 * @exclude
 */
public class CameraResolutionRequirement implements Requirement {

    // We require ~8MP or higher picture resolutions
    public static final int MIN_PICTURE_AREA = 7_900_000;
    // We allow up to 13MP picture resolutions
    public static final int MAX_PICTURE_AREA = 13_000_000;

    private final CameraHolder mCameraHolder;

    CameraResolutionRequirement(final CameraHolder cameraHolder) {
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
            final Camera.Parameters parameters = mCameraHolder.getCameraParameters();
            if (parameters != null) {
                final Pair<Size, Size> sizes = SizeSelectionHelper.getBestSize(parameters.getSupportedPictureSizes(),
                        parameters.getSupportedPreviewSizes(),
                        MAX_PICTURE_AREA,
                        MIN_PICTURE_AREA
                );
                if (sizes == null) {
                    result = false;
                    details = "Camera doesn't have a resolution that matches the requirements";
                    return new RequirementReport(getId(), result, details);
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
