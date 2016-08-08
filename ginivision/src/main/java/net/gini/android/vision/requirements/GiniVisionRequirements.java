package net.gini.android.vision.requirements;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public final class GiniVisionRequirements {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionRequirements.class);

    public static RequirementsReport checkRequirements(Context context) {
        LOG.info("Checking requirements");
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

        LOG.info("Requirements checked with results: {}", requirementsReport);
        return requirementsReport;
    }

}
