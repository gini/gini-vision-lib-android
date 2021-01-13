package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.requirements.TestUtil.createSize;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.hardware.Camera;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class CameraResolutionRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifNoPreviewSize_withSameAspectRatio_asLargestPictureSize() {
        CameraHolder cameraHolder = getCameraHolder(Collections.singletonList(createSize(300, 200)),
                null);

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
        assertThat(requirement.check().getDetails()).isEqualTo(
                "Camera doesn't have a resolution that matches the requirements");
    }

    @Test
    public void should_reportUnfulfilled_ifPictureSize_isSmallerThan8MP() {
        CameraHolder cameraHolder = getCameraHolder(null,
                Arrays.asList(
                        createSize(400, 300),
                        createSize(3200, 2048)) //6,55MP
        );

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
        assertThat(requirement.check().getDetails()).isEqualTo(
                "Camera doesn't have a resolution that matches the requirements");
    }

    @Test
    public void should_reportFulfilled_ifPreviewSize_andPictureSize_isLargerThan8MP() {
        CameraHolder cameraHolder = getCameraHolder(null, null);

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isTrue();
        assertThat(requirement.check().getDetails()).isEqualTo("");
    }

    @Test
    public void should_reportUnfulfilled_ifCamera_isNotOpen() {
        CameraHolder cameraHolder = mock(CameraHolder.class);

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
        assertThat(requirement.check().getDetails()).isEqualTo("Camera not open");
    }

    private CameraHolder getCameraHolder(List<Camera.Size> previewSizes,
            List<Camera.Size> pictureSizes) {
        CameraHolder cameraHolder = mock(CameraHolder.class);
        Camera.Parameters parameters = mock(Camera.Parameters.class);
        when(cameraHolder.getCameraParameters()).thenReturn(parameters);
        if (previewSizes == null) {
            Camera.Size size4to3 = createSize(1440, 1080);
            Camera.Size size16to9 = createSize(1280, 720);
            previewSizes = Arrays.asList(size4to3, size16to9);
        }
        if (pictureSizes == null) {
            Camera.Size size4to3 = createSize(2880, 2160);
            Camera.Size size16to9 = createSize(3840, 2160);
            pictureSizes = Arrays.asList(size4to3, size16to9);
        }
        when(parameters.getSupportedPreviewSizes()).thenReturn(previewSizes);
        when(parameters.getSupportedPictureSizes()).thenReturn(pictureSizes);
        return cameraHolder;
    }
}
