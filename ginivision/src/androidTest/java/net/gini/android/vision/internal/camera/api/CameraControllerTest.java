package net.gini.android.vision.internal.camera.api;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.prepareLooper;
import static net.gini.android.vision.test.PermissionsHelper.grantCameraPermission;

import static org.junit.Assume.assumeTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.content.Intent;
import android.hardware.Camera;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.gini.android.vision.internal.camera.photo.Size;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class CameraControllerTest {

    private ActivityTestRule<NoOpActivity> mIntentsTestRule = new ActivityTestRule<>(
            NoOpActivity.class);

    private CameraController mCameraController;

    @Before
    public void setUp() throws InterruptedException {
        prepareLooper();
        grantCameraPermission();
    }

    @After
    public void tearDown() throws Exception {
        if (mCameraController != null) {
            mCameraController.close();
        }
    }

    private NoOpActivity createNoOpActivity() {
        return launchNoOpActivity();
    }

    private Camera openAndGetCamera() throws InterruptedException {
        mCameraController.open().join();
        Camera camera = mCameraController.getCamera();
        assertThat(camera).isNotNull();
        return camera;
    }

    private NoOpActivity launchNoOpActivity() {
        final Intent intent = new Intent(getTargetContext(),
                NoOpActivity.class);
        return mIntentsTestRule.launchActivity(intent);
    }

    @Test
    public void should_useLargestPictureResolution() throws InterruptedException {
        mCameraController = new CameraController(createNoOpActivity());
        final Camera.Parameters parameters = openAndGetCamera().getParameters();
        final Size largestSize = SizeSelectionHelper.getLargestSize(parameters.getSupportedPictureSizes());
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPictureSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }

    @Test
    public void should_useLargestPreviewResolution_withSimilarAspectRatio_asPictureSize() throws InterruptedException {
        mCameraController = new CameraController(createNoOpActivity());
        final Camera.Parameters parameters = openAndGetCamera().getParameters();
        final Size pictureSize = new Size(parameters.getPictureSize().width, parameters.getPictureSize().height);
        final Size largestSize = SizeSelectionHelper.getLargestSizeWithSimilarAspectRatio(parameters.getSupportedPreviewSizes(), pictureSize);
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPreviewSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }

    @Test
    public void should_useContinuousFocusMode_ifAvailable() {
        assumeTrue("Camera supports continuous focus mode", hasCameraContinuousFocusMode());
        mCameraController =
                new CameraControllerWithMockableCamera(createNoOpActivity());
        Camera cameraSpy = openCameraAndGetCameraSpyWithSupportedFocusModes(
                (CameraControllerWithMockableCamera) mCameraController,
                Arrays.asList(Camera.Parameters.FOCUS_MODE_AUTO,
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE));

        Camera.Parameters usedParameters = cameraSpy.getParameters();
        assertThat(usedParameters.getFocusMode()).isEqualTo(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    private boolean hasCameraContinuousFocusMode() {
        Camera camera = Camera.open();
        boolean continuousFocusMode = camera.getParameters().getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.release();
        return continuousFocusMode;
    }

    private Camera openCameraAndGetCameraSpyWithSupportedFocusModes(CameraControllerWithMockableCamera cameraController, List<String> focusModes) {
        Camera camera = Camera.open();
        Camera cameraSpy = spy(camera);
        Camera.Parameters parametersSpy = spy(camera.getParameters());

        doReturn(parametersSpy).when(cameraSpy).getParameters();
        doReturn(focusModes).when(parametersSpy).getSupportedFocusModes();
        cameraController.setMockCamera(cameraSpy);

        cameraController.open().join();

        return cameraSpy;
    }

    @Test
    public void should_useAutoFocusMode_ifContinuousFocusMode_isNotAvailable() {
        assumeTrue("Camera supports auto focus mode", hasCameraAutoFocusMode());
        mCameraController =
                new CameraControllerWithMockableCamera(createNoOpActivity());
        Camera cameraSpy = openCameraAndGetCameraSpyWithSupportedFocusModes(
                (CameraControllerWithMockableCamera) mCameraController,
                Collections.singletonList(Camera.Parameters.FOCUS_MODE_AUTO));

        Camera.Parameters usedParameters = cameraSpy.getParameters();
        assertThat(usedParameters.getFocusMode()).isEqualTo(Camera.Parameters.FOCUS_MODE_AUTO);
    }

    private boolean hasCameraAutoFocusMode() {
        Camera camera = Camera.open();
        boolean autoFocusMode = camera.getParameters().getSupportedFocusModes().contains(
                Camera.Parameters.FOCUS_MODE_AUTO);
        camera.release();
        return autoFocusMode;
    }

    @Test
    public void should_doAutoFocusRun_beforeTakingPicture_ifNoContinuousFocusMode() {
        mCameraController =
                spy(new CameraControllerWithMockedFocusingAndPictureTaking(createNoOpActivity()));
        openCameraAndGetCameraSpyWithSupportedFocusModes(
                (CameraControllerWithMockableCamera) mCameraController,
                Collections.singletonList(Camera.Parameters.FOCUS_MODE_AUTO));

        mCameraController.takePicture().join();

        verify(mCameraController).focus();
    }

    @Test
    public void should_notDoAutoFocusRun_beforeTakingPicture_ifUsingContinuousFocusMode() {
        assumeTrue("Camera supports continuous focus mode", hasCameraContinuousFocusMode());
        mCameraController =
                spy(new CameraControllerWithMockedFocusingAndPictureTaking(createNoOpActivity()));
        openCameraAndGetCameraSpyWithSupportedFocusModes(
                (CameraControllerWithMockableCamera) mCameraController,
                Arrays.asList(Camera.Parameters.FOCUS_MODE_AUTO,
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE));

        mCameraController.takePicture().join();

        verify(mCameraController, times(0)).focus();
    }
}