package net.gini.android.vision.requirements;

import android.content.Context;

import java.util.Arrays;

public final class GiniVisionRequirements {

    public static RequirementsReport checkRequirements(Context context) {
        CameraHolder cameraHolder = new CameraHolder();

        RequirementsReport requirementsReport = new RequirementsChecker(Arrays.asList(
                new CameraPermissionRequirement(context),
                new CameraRequirement(cameraHolder),
                new CameraResolutionRequirement(cameraHolder),
                new CameraFlashRequirement(cameraHolder),
                new CameraAutoFocusRequirement(cameraHolder),
                new DeviceMemoryRequirement(cameraHolder)
        )).checkRequirements();

        cameraHolder.closeCamera();

        return requirementsReport;
    }

}
