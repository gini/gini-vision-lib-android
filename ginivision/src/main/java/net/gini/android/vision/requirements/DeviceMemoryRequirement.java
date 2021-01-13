package net.gini.android.vision.requirements;

import android.hardware.Camera;

import net.gini.android.vision.internal.camera.api.SizeSelectionHelper;
import net.gini.android.vision.internal.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.util.Pair;

class DeviceMemoryRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    DeviceMemoryRequirement(final CameraHolder cameraHolder) {
        mCameraHolder = cameraHolder;
    }

    @NonNull
    @Override
    public RequirementId getId() {
        return RequirementId.DEVICE_MEMORY;
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
                        CameraResolutionRequirement.MAX_PICTURE_AREA,
                        CameraResolutionRequirement.MIN_PICTURE_AREA);
                if (sizes == null) {
                    result = false;
                    details =
                            "Cannot determine memory requirement as the camera has no picture resolution with a 4:3 aspect ratio";
                } else if (!sufficientMemoryAvailable(sizes.first)) {
                    result = false;
                    details = "Insufficient memory available";
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

    /**
     * Given a photo size, return whether there is (currently) enough memory available.
     *
     * @param photoSize the size of photos that will be used for image processing
     *
     * @return whether there is enough memory for the image processing to succeed
     */
    @VisibleForTesting
    boolean sufficientMemoryAvailable(final Size photoSize) {
        final Runtime runtime = Runtime.getRuntime();
        return sufficientMemoryAvailable(runtime, photoSize);
    }

    @VisibleForTesting
    boolean sufficientMemoryAvailable(final Runtime runtime,
            final Size photoSize) {
        final float memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / 1024f / 1024f;
        final float memoryNeeded = calculateMemoryUsageForSize(photoSize) / 1024f / 1024f;
        final float maxMemory = runtime.maxMemory() / 1024f / 1024f;
        return memoryNeeded + memoryUsed < maxMemory;
    }

    private float calculateMemoryUsageForSize(final Size photoSize) {
        // We have three channels of one byte each and we hold about three pictures in memory.
        return photoSize.width * photoSize.height * 3 * 3;
    }
}
