package net.gini.android.vision.requirements;

import android.hardware.Camera;

import androidx.annotation.NonNull;

import net.gini.android.vision.internal.camera.api.SizeSelectionHelper;
import net.gini.android.vision.internal.util.Size;

import java.util.Locale;

/**
 * Internal use only.
 *
 * @exclude
 */
public class CameraResolutionRequirement implements Requirement {

    // We require ~8MP or higher picture resolutions
    private static final int MIN_PICTURE_AREA = 7_900_000;
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
                final Size pictureSize = SizeSelectionHelper.getLargestAllowedSize(
                        parameters.getSupportedPictureSizes(), MAX_PICTURE_AREA);
                if (pictureSize == null) {
                    result = false;
                    details = "Camera has no picture resolutions";
                    return new RequirementReport(getId(), result, details);
                } else if (!isAround8MPOrHigher(pictureSize)) {
                    result = false;
                    details = "Largest camera picture resolution is lower than 8MP";
                    return new RequirementReport(getId(), result, details);
                }

                final Size previewSize = SizeSelectionHelper.getLargestAllowedSizeWithSimilarAspectRatio(
                        parameters.getSupportedPreviewSizes(), pictureSize, MAX_PICTURE_AREA);
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
        } catch (final RuntimeException e) {
            result = false;
            details = "Camera exception: " + e.getMessage();
        }

        return new RequirementReport(getId(), result, details);
    }

    private boolean isAround8MPOrHigher(final Size size) {
        return size.width * size.height >= MIN_PICTURE_AREA;
    }

}
