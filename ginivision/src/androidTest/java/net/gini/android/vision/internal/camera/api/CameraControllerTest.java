package net.gini.android.vision.internal.camera.api;

import static android.support.test.InstrumentationRegistry.getTargetContext;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.prepareLooper;
import static net.gini.android.vision.test.PermissionsHelper.grantCameraPermission;

import android.content.Intent;
import android.hardware.Camera;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.gini.android.vision.internal.util.Size;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CameraControllerTest {

    private ActivityTestRule<NoOpActivity> mIntentsTestRule = new ActivityTestRule<>(
            NoOpActivity.class, true, false);

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

    @Test
    public void should_useLargestPictureResolution() throws InterruptedException {
        mCameraController = new CameraController(createNoOpActivity());
        final Camera.Parameters parameters = openAndGetCamera().getParameters();
        final Size largestSize = SizeSelectionHelper.getLargestSize(
                parameters.getSupportedPictureSizes());
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPictureSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }

    private NoOpActivity createNoOpActivity() {
        return launchNoOpActivity();
    }

    private NoOpActivity launchNoOpActivity() {
        final Intent intent = new Intent(getTargetContext(),
                NoOpActivity.class);
        return mIntentsTestRule.launchActivity(intent);
    }

    private Camera openAndGetCamera() throws InterruptedException {
        mCameraController.open().join();
        Camera camera = mCameraController.getCamera();
        assertThat(camera).isNotNull();
        return camera;
    }

    @Test
    public void should_useLargestPreviewResolution_withSimilarAspectRatio_asPictureSize()
            throws InterruptedException {
        mCameraController = new CameraController(createNoOpActivity());
        final Camera.Parameters parameters = openAndGetCamera().getParameters();
        final Size pictureSize = new Size(parameters.getPictureSize().width,
                parameters.getPictureSize().height);
        final Size largestSize = SizeSelectionHelper.getLargestSizeWithSimilarAspectRatio(
                parameters.getSupportedPreviewSizes(), pictureSize);
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPreviewSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }

    @Test
    @Ignore
    public void should_useContinuousFocusMode_ifAvailable() {
        // TODO: implement w/o using a camera spy
    }

    @Test
    @Ignore
    public void should_useAutoFocusMode_ifContinuousFocusMode_isNotAvailable() {
        // TODO: implement w/o using a camera spy
    }

    @Test
    @Ignore
    public void should_doAutoFocusRun_beforeTakingPicture_ifNoContinuousFocusMode() {
        // TODO: implement w/o using a camera spy
    }

    @Test
    @Ignore
    public void should_notDoAutoFocusRun_beforeTakingPicture_ifUsingContinuousFocusMode() {
        // TODO: implement w/o using a camera spy
    }
}