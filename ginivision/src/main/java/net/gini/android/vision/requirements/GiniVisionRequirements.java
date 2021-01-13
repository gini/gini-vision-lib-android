package net.gini.android.vision.requirements;

import static net.gini.android.vision.internal.util.ContextHelper.isTablet;

import android.content.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;

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
    public static RequirementsReport checkRequirements(final Context context) {
        LOG.info("Checking requirements");
        final CameraHolder cameraHolder = new CameraHolder();

        final List<Requirement> requirements;
        if (isTablet(context)) {
            requirements = getTabletRequirements(context, cameraHolder);
        } else {
            requirements = getPhoneRequirements(context, cameraHolder);
        }

        final RequirementsReport requirementsReport = new RequirementsChecker(requirements)
                .checkRequirements();

        cameraHolder.closeCamera();

        LOG.info("Requirements checked with results: {}", requirementsReport);
        return requirementsReport;
    }

    @NonNull
    private static List<Requirement> getPhoneRequirements(final Context context,
            final CameraHolder cameraHolder) {
        return Arrays.asList(
                new CameraPermissionRequirement(context),
                new CameraRequirement(cameraHolder),
                new CameraResolutionRequirement(cameraHolder),
                new CameraFlashRequirement(cameraHolder),
                new CameraFocusRequirement(cameraHolder),
                new DeviceMemoryRequirement(cameraHolder)
        );
    }

    @NonNull
    private static List<Requirement> getTabletRequirements(final Context context,
            final CameraHolder cameraHolder) {
        return Arrays.asList(
                new CameraPermissionRequirement(context),
                new CameraRequirement(cameraHolder),
                new CameraResolutionRequirement(cameraHolder),
                new CameraFocusRequirement(cameraHolder),
                new DeviceMemoryRequirement(cameraHolder)
        );
    }

    private GiniVisionRequirements() {
    }
}
