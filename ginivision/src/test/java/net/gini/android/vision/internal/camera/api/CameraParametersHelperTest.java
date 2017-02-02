package net.gini.android.vision.internal.camera.api;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.hardware.Camera;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;

@RunWith(JUnit4.class)
public class CameraParametersHelperTest {

    private Camera mCamera;
    private Camera.Parameters mParameters;

    @Before
    public void setUp() throws Exception {
        // Mock camera and parameters
        mCamera = mock(Camera.class);
        mParameters = mock(Camera.Parameters.class);
        when(mCamera.getParameters()).thenReturn(mParameters);
    }

    @Test
    public void should_verifyThatFocusMode_isSupported() {
        // Mock supported focus modes
        when(mParameters.getSupportedFocusModes()).thenReturn(
                Arrays.asList(Camera.Parameters.FOCUS_MODE_AUTO,
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE));

        boolean isSupported = CameraParametersHelper.isFocusModeSupported(
                Camera.Parameters.FOCUS_MODE_AUTO, mCamera);

        assertThat(isSupported).isTrue();
    }

    @Test
    public void should_verifyThatFocusMode_isNotSupported() {
        // Mock supported focus modes
        when(mParameters.getSupportedFocusModes()).thenReturn(
                Collections.singletonList(Camera.Parameters.FOCUS_MODE_AUTO));

        boolean isSupported = CameraParametersHelper.isFocusModeSupported(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, mCamera);

        assertThat(isSupported).isFalse();
    }

    @Test
    public void should_verifyThatFocusMode_isUsed() {
        // Mock used focus mode
        when(mParameters.getFocusMode()).thenReturn(Camera.Parameters.FOCUS_MODE_AUTO);

        boolean isUsed = CameraParametersHelper.isUsingFocusMode(
                Camera.Parameters.FOCUS_MODE_AUTO, mCamera);

        assertThat(isUsed).isTrue();
    }

    @Test
    public void should_verifyThatFocusMode_isNotUsed() {
        // Mock used focus mode
        when(mParameters.getFocusMode()).thenReturn(Camera.Parameters.FOCUS_MODE_AUTO);

        boolean isUsed = CameraParametersHelper.isUsingFocusMode(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE, mCamera);

        assertThat(isUsed).isFalse();
    }

    @Test
    public void should_verifyThatFlashMode_isSupported() {
        // Mock used flash mode
        when(mParameters.getSupportedFlashModes())
                .thenReturn(Arrays.asList(Camera.Parameters.FLASH_MODE_AUTO,
                        Camera.Parameters.FLASH_MODE_TORCH));

        boolean isSupported = CameraParametersHelper.isFlashModeSupported(
                Camera.Parameters.FLASH_MODE_AUTO, mCamera);

        assertThat(isSupported).isTrue();
    }

    @Test
    public void should_verifyThatFlashMode_isNotSupported() {
        // Mock used flash mode
        when(mParameters.getSupportedFlashModes())
                .thenReturn(Collections.singletonList(Camera.Parameters.FLASH_MODE_AUTO));

        boolean isSupported = CameraParametersHelper.isFlashModeSupported(
                Camera.Parameters.FLASH_MODE_TORCH, mCamera);

        assertThat(isSupported).isFalse();
    }
}