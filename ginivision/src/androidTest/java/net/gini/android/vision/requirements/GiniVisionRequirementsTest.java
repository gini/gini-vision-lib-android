package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertWithMessage;

import static net.gini.android.vision.test.PermissionsHelper.grantCameraPermission;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class GiniVisionRequirementsTest {

    private static final String LOG_TAG = "RequirementsReport";

    // Intended to gather requirement reports from devices.
    // This test should be ignored normally.
    // @Ignore
    @Test
    public void should_fulfillRequirements() throws InterruptedException {
        grantCameraPermission();
        final RequirementsReport report = GiniVisionRequirements.checkRequirements(
                InstrumentationRegistry.getTargetContext());
        assertWithMessage(unfulfilledRequirementsMessage(report))
                .that(report.isFulfilled()).isTrue();
    }

    private String unfulfilledRequirementsMessage(final RequirementsReport report) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!report.isFulfilled()) {
            stringBuilder.append("Some requirements were not met");
            stringBuilder.append("\n");
            for (final RequirementReport requirementReport : report.getRequirementReports()) {
                if (!requirementReport.isFulfilled()) {
                    stringBuilder.append(requirementReport.getRequirementId());
                    stringBuilder.append(": ");
                    stringBuilder.append(requirementReport.getDetails());
                    stringBuilder.append("\n");
                }
            }
        }
        return stringBuilder.toString();
    }
}