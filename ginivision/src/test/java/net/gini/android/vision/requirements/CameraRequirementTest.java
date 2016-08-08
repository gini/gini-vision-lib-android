package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.hardware.Camera;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CameraRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifCamera_couldNotBeOpened() {
        CameraHolder cameraHolder = mock(CameraHolder.class);
        doThrow(new RuntimeException()).when(cameraHolder).openCamera();

        CameraRequirement cameraRequirement = new CameraRequirement(cameraHolder);

        assertThat(cameraRequirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportUnfulfilled_ifCamera_isNull() {
        CameraHolder cameraHolder = mock(CameraHolder.class);
        when(cameraHolder.getCamera()).thenReturn(null);

        CameraRequirement cameraRequirement = new CameraRequirement(cameraHolder);

        assertThat(cameraRequirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportFulfilled_ifCamera_couldBeOpened_andIsNotNull() {
        CameraHolder cameraHolder = mock(CameraHolder.class);
        when(cameraHolder.getCamera()).thenReturn(mock(Camera.class));

        CameraRequirement cameraRequirement = new CameraRequirement(cameraHolder);

        assertThat(cameraRequirement.check().isFulfilled()).isTrue();
    }
}
