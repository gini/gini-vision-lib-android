package net.gini.android.vision.requirements;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * <p>
 *     Checks the device's hardware capabilities to determine, if it is compatible with the Gini Vision Library.
 * </p>
 * <p>
 *     The checked requirements are listed in the {@link RequirementId} enum.
 * </p>
 * <p>
 *     Call {@link GiniVisionRequirements#checkRequirements(Context)} to get a report of the requirement checks.
 * </p>
 * <p>
 *     On Android 6.0 and later you need to ask the user for the camera permission before you check the requirements.
 * </p>
 */
public final class GiniVisionRequirements {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionRequirements.class);

    /**
     * <p>
     *     Checks the device's hardware capabilities.
     * </p>
     * @param context any {@link Context} instance
     * @return {@link RequirementsReport} containing information about the checks
     */
    public static RequirementsReport checkRequirements(Context context) {
        LOG.info("Checking requirements");
        CameraHolder cameraHolder = new CameraHolder();

        RequirementsReport requirementsReport = new RequirementsChecker(Arrays.asList(
                new CameraPermissionRequirement(context),
                new CameraRequirement(cameraHolder),
                new CameraResolutionRequirement(cameraHolder),
                new CameraFlashRequirement(cameraHolder),
                new CameraFocusRequirement(cameraHolder),
                new DeviceMemoryRequirement(cameraHolder)
        )).checkRequirements();

        cameraHolder.closeCamera();

        LOG.info("Requirements checked with results: {}", requirementsReport);
        return requirementsReport;
    }

    private GiniVisionRequirements() {
    }
}
