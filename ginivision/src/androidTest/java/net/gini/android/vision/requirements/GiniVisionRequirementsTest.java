package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertWithMessage;

import static net.gini.android.vision.test.PermissionsHelper.grantCameraPermission;

import android.hardware.Camera;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Locale;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class GiniVisionRequirementsTest {

    private static final String LOG_TAG = "RequirementsReport";

    @Ignore("Intended to gather requirement reports from devices.")
    @Test
    public void should_fulfillRequirements() throws InterruptedException {
        grantCameraPermission();
        final RequirementsReport report = GiniVisionRequirements.checkRequirements(
                ApplicationProvider.getApplicationContext());
        assertWithMessage(unfulfilledRequirementsMessage(report))
                .that(report.isFulfilled()).isTrue();
    }

    private String unfulfilledRequirementsMessage(final RequirementsReport report) {
        final StringBuilder stringBuilder = new StringBuilder();
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
                if (requirementReport.getRequirementId() == RequirementId.CAMERA_RESOLUTION) {
                    appendResolutions(stringBuilder);
                }
            }
        }
        return stringBuilder.toString();
    }

    private void appendResolutions(final StringBuilder stringBuilder) {
        final Camera camera = Camera.open();
        stringBuilder.append("Picture resolutions:\n");
        appendPictureResolutions(stringBuilder, camera);
        stringBuilder.append("Preview resolutions:\n");
        appendPreviewResolutions(stringBuilder, camera);
        if (camera != null) {
            camera.release();
        }
    }

    private void appendPictureResolutions(final StringBuilder stringBuilder, final Camera camera) {
        final List<Camera.Size> sizes = camera.getParameters().getSupportedPictureSizes();
        appendSizes(stringBuilder, sizes);
    }

    private void appendSizes(final StringBuilder stringBuilder, final List<Camera.Size> sizes) {
        stringBuilder.append("Resolution, Megapixel, Aspect Ratio\n");
        for (final Camera.Size size : sizes) {
            stringBuilder.append(
                    String.format(Locale.US, "%dx%d, %.1fMP, %.6f", size.width, size.height,
                            (float) size.width * size.height / 1_000_000f,
                            (float) size.width / size.height))
                    .append("\n");
        }
    }

    private void appendPreviewResolutions(final StringBuilder stringBuilder, final Camera camera) {
        final List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
        appendSizes(stringBuilder, sizes);
    }
}