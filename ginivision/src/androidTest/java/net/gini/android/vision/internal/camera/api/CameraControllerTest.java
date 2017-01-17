package net.gini.android.vision.internal.camera.api;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.prepareLooper;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import net.gini.android.vision.internal.camera.photo.Size;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CameraControllerTest {

    private ActivityTestRule<NoOpActivity> mIntentsTestRule = new ActivityTestRule<>(
            NoOpActivity.class);

    private CameraController mCameraController;

    @Before
    public void setUp() {
        prepareLooper();
        grantCameraPermission();
        createCameraController();
    }

    public void grantCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getInstrumentation().getUiAutomation().executeShellCommand(
                    "pm grant " + getTargetContext().getPackageName()
                            + " android.permission.CAMERA");
        }
    }

    private void createCameraController() {
        final NoOpActivity activity = launchNoOpActivity();
        mCameraController = new CameraController(activity);
    }

    private NoOpActivity launchNoOpActivity() {
        final Intent intent = new Intent(getTargetContext(),
                NoOpActivity.class);
        return mIntentsTestRule.launchActivity(intent);
    }

    @Test
    public void should_useLargestPictureResolution() {
        Camera camera = getCamera();
        final Camera.Parameters parameters = camera.getParameters();
        final Size largestSize = SizeSelectionHelper.getLargestSize(parameters.getSupportedPictureSizes());
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPictureSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }

    private Camera getCamera() {
        mCameraController.open().join();
        Camera camera = mCameraController.getCamera();
        assertThat(camera).isNotNull();
        return camera;
    }

    @Test
    public void should_useLargestPreviewResolution_withSameAspectRatio_asPictureSize() {
        Camera camera = getCamera();
        final Camera.Parameters parameters = camera.getParameters();
        final Size pictureSize = new Size(parameters.getPictureSize().width, parameters.getPictureSize().height);
        final Size largestSize = SizeSelectionHelper.getLargestSizeWithSameAspectRatio(parameters.getSupportedPreviewSizes(), pictureSize);
        assertThat(largestSize).isNotNull();
        final Camera.Size usedSize = parameters.getPreviewSize();
        assertThat(usedSize.width).isEqualTo(largestSize.width);
        assertThat(usedSize.height).isEqualTo(largestSize.height);
    }
}