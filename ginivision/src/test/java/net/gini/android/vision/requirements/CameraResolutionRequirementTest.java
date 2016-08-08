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
import java.util.List;

@RunWith(JUnit4.class)
public class CameraResolutionRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifNoPreviewSize_with4to3AspectRatio() {
        CameraHolder cameraHolder = getCameraHolder(Arrays.asList(createSize(300, 200), createSize(600, 400)), null);

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportUnfulfilled_ifNoPictureSize_with4to3AspectRatio() {
        CameraHolder cameraHolder = getCameraHolder(null, Arrays.asList(createSize(4000, 2000), createSize(3000, 3000)));

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportUnfulfilled_ifPictureSize_isSmallerThan8MP() {
        CameraHolder cameraHolder = getCameraHolder(null,
                Arrays.asList(
                        createSize(400, 300),
                        createSize(3200, 2400)) //7,68MP
        );

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportFulfilled_ifPreviewSize_andPictureSize_with4to3AspectRatio_andPictureSize_isLargerThan8MP() {
        CameraHolder cameraHolder = getCameraHolder(null,null);

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isTrue();
    }

    @Test
    public void should_reportUnfulfilled_ifCamera_isNotOpen() {
        CameraHolder cameraHolder = mock(CameraHolder.class);

        CameraResolutionRequirement requirement = new CameraResolutionRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    private CameraHolder getCameraHolder(List<Camera.Size> previewSizes, List<Camera.Size> pictureSizes) {
        CameraHolder cameraHolder = mock(CameraHolder.class);
        Camera camera = mock(Camera.class);
        Camera.Parameters parameters = mock(Camera.Parameters.class);
        when(cameraHolder.getCamera()).thenReturn(camera);
        when(camera.getParameters()).thenReturn(parameters);
        if (previewSizes == null) {
            Camera.Size size4to3 = createSize(1440, 1080);
            Camera.Size sizeOther = createSize(1280, 720);
            previewSizes = Arrays.asList(size4to3, sizeOther);
        }
        if (pictureSizes == null) {
            Camera.Size size4to3 = createSize(4128, 3096);
            Camera.Size sizeOther = createSize(4128, 2322);
            pictureSizes = Arrays.asList(size4to3, sizeOther);
        }
        when(parameters.getSupportedPreviewSizes()).thenReturn(previewSizes);
        when(parameters.getSupportedPictureSizes()).thenReturn(pictureSizes);
        return cameraHolder;
    }
}
