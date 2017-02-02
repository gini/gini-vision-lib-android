package net.gini.android.vision.requirements;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.hardware.Camera;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;

@RunWith(JUnit4.class)
public class CameraFocusRequirementTest {

    @Test
    public void should_reportUnfulfilled_ifAutoFocus_isNotSupported() {
        CameraHolder cameraHolder = getCameraHolder(false);

        CameraFocusRequirement requirement = new CameraFocusRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    @Test
    public void should_reportFulfilled_ifAutoFocus_isSupported() {
        CameraHolder cameraHolder = getCameraHolder(true);

        CameraFocusRequirement requirement = new CameraFocusRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isTrue();
    }

    @Test
    public void should_reportUnfulfilled_ifCamera_isNotOpen() {
        CameraHolder cameraHolder = mock(CameraHolder.class);

        CameraFocusRequirement requirement = new CameraFocusRequirement(cameraHolder);

        assertThat(requirement.check().isFulfilled()).isFalse();
    }

    public CameraHolder getCameraHolder(boolean isAutoFocusSupported) {
        CameraHolder cameraHolder = mock(CameraHolder.class);
        Camera.Parameters parameters = mock(Camera.Parameters.class);
        when(cameraHolder.getCameraParameters()).thenReturn(parameters);
        when(parameters.getSupportedFocusModes()).thenReturn(
                isAutoFocusSupported ?
                        Collections.singletonList(Camera.Parameters.FOCUS_MODE_AUTO)
                        : Collections.singletonList(Camera.Parameters.FOCUS_MODE_FIXED));

        return cameraHolder;
    }
}
