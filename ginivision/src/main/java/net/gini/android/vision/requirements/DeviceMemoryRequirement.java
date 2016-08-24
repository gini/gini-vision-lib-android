package net.gini.android.vision.requirements;

import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.internal.camera.api.Util;
import net.gini.android.vision.internal.camera.photo.Size;

class DeviceMemoryRequirement implements Requirement {

    private final CameraHolder mCameraHolder;

    DeviceMemoryRequirement(CameraHolder cameraHolder) {
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
            Camera.Parameters parameters = mCameraHolder.getCameraParameters();
            if (parameters != null) {
                Size pictureSize = Util.getLargestFourThreeRatioSize(parameters.getSupportedPictureSizes());
                if (pictureSize == null) {
                    result = false;
                    details = "Cannot determine memory requirement as the camera has no picture resolution with a 4:3 aspect ratio";
                } else if (!sufficientMemoryAvailable(pictureSize)) {
                    result = false;
                    details = "Insufficient memory available";
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

    /**
     * Given a photo size, return whether there is (currently) enough memory available.
     *
     * @param photoSize       the size of photos that will be used for image processing
     * @return whether there is enough memory for the image processing to succeed
     */
    @VisibleForTesting
    boolean sufficientMemoryAvailable(Size photoSize) {
        Runtime runtime = Runtime.getRuntime();
        return sufficientMemoryAvailable(runtime, photoSize);
    }

    @VisibleForTesting
    boolean sufficientMemoryAvailable(Runtime runtime,
                                      final Size photoSize) {
        final float memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        final float memoryNeeded = calculateMemoryUsageForSize(photoSize) / 1024 / 1024;
        final float maxMemory = runtime.maxMemory() / 1024 / 1024;
        return memoryNeeded + memoryUsed < maxMemory;
    }

    private float calculateMemoryUsageForSize(final Size photoSize) {
        // We have three channels of one byte each and we hold about three pictures in memory.
        return photoSize.width * photoSize.height * 3 * 3;
    }
}
