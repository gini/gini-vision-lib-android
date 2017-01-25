package net.gini.android.vision.internal.camera.api;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.prepareLooper;
import static net.gini.android.vision.test.PermissionsHelper.grantCameraPermission;

import android.content.Intent;
import android.hardware.Camera;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.gini.android.vision.internal.camera.photo.Size;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CameraControllerTest {

    private ActivityTestRule<NoOpActivity> mIntentsTestRule = new ActivityTestRule<>(
            NoOpActivity.class);

    private CameraController mCameraController;

    private Camera mCamera;

    @Before
    public void setUp() throws InterruptedException {
        prepareLooper();
        grantCameraPermission();
        createCameraController();
        mCamera = getCamera();
    }

    @After
    public void tearDown() throws Exception {
        mCameraController.close();
    }

    private void createCameraController() {
        final NoOpActivity activity = launchNoOpActivity();
        mCameraController = new CameraController(activity);
    }

    private Camera getCamera() throws InterruptedException {
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
    public void should_useLargestPictureResolution() {
        final Camera.Parameters parameters = mCamera.getParameters();
        final Size largestSize = SizeSelectionHelper.getLargestSize(parameters.getSupportedPictureSizes());
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPictureSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }

    @Test
    public void should_useLargestPreviewResolution_withSimilarAspectRatio_asPictureSize() {
        final Camera.Parameters parameters = mCamera.getParameters();
        final Size pictureSize = new Size(parameters.getPictureSize().width, parameters.getPictureSize().height);
        final Size largestSize = SizeSelectionHelper.getLargestSizeWithSimilarAspectRatio(parameters.getSupportedPreviewSizes(), pictureSize);
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPreviewSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }
}