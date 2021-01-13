package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.requirements.TestUtil.createSize;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import android.hardware.Camera;

import net.gini.android.vision.internal.util.Size;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(JUnit4.class)
public class DeviceMemoryRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifCamera_isNotOpen() {
        CameraHolder cameraHolder = mock(CameraHolder.class);

        DeviceMemoryRequirement requirement = new DeviceMemoryRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportUnfulfilled_ifEnoughMemory_isNotAvailable() {
        CameraHolder cameraHolder = getCameraHolder(null);

        DeviceMemoryRequirement requirement = spy(new DeviceMemoryRequirement(cameraHolder));

        doReturn(false).when(requirement).sufficientMemoryAvailable(any(Size.class));

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportFulfilled_ifEnoughMemory_isAvailable() {
        CameraHolder cameraHolder = getCameraHolder(
                Collections.singletonList(createSize(3264, 2448)));

        DeviceMemoryRequirement requirement = new DeviceMemoryRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isTrue();
    }

    @Test
    public void should_checkIfPictureSize_fitsIntoUnusedMemory() {
        CameraHolder cameraHolder = getCameraHolder(null);
        DeviceMemoryRequirement requirement = new DeviceMemoryRequirement(cameraHolder);

        // Unused memory = max - (total - free)
        Runtime runtime = getRuntimeMock(28, 1, 32);

        // Required memory: size.width * size.height * 3 * 3
        assertThat(requirement.sufficientMemoryAvailable(runtime, new Size(800, 600))).isTrue();
        assertThat(requirement.sufficientMemoryAvailable(runtime, new Size(1024, 768))).isFalse();
    }

    private CameraHolder getCameraHolder(List<Camera.Size> pictureSizes) {
        CameraHolder cameraHolder = mock(CameraHolder.class);
        Camera.Parameters parameters = mock(Camera.Parameters.class);
        when(cameraHolder.getCameraParameters()).thenReturn(parameters);
        if (pictureSizes == null) {
            Camera.Size size4to3 = createSize(4128, 3096);
            Camera.Size sizeOther = createSize(4128, 2322);
            pictureSizes = Arrays.asList(size4to3, sizeOther);
        }
        when(parameters.getSupportedPictureSizes()).thenReturn(pictureSizes);
        when(parameters.getSupportedPreviewSizes()).thenReturn(pictureSizes);
        return cameraHolder;
    }

    private Runtime getRuntimeMock(int totalMemoryMbs, int freeMemoryMbs, int maxMemoryMbs) {
        // Memory used = total memory - free memory
        Runtime runtime = mock(Runtime.class);
        when(runtime.totalMemory()).thenReturn(totalMemoryMbs * 1024L * 1024L);
        when(runtime.freeMemory()).thenReturn(freeMemoryMbs * 1024L * 1024L);
        // Max memory
        when(runtime.maxMemory()).thenReturn(maxMemoryMbs * 1024L * 1024L);
        return runtime;
    }
}
