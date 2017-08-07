package net.gini.android.vision.uiautomator;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.PermissionsHelper.grantCameraPermission;
import static net.gini.android.vision.uiautomator.Helper.openApp;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;

import net.gini.android.vision.test.ScreenshotHelper;
import net.gini.android.vision.uiautomator.screens.screenapi.CameraScreen;
import net.gini.android.vision.uiautomator.screens.screenapi.MainScreen;
import net.gini.android.vision.uiautomator.screens.screenapi.ReviewScreen;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.Date;

/**
 * <p>
 * Used for taking screenshots of the different screens.
 * </p>
 * <p>
 * Enable it only when you need screenshots. It should be disabled most of the time.
 * </p>
 */
@Ignore
@SdkSuppress(minSdkVersion = 18)
@RunWith(AndroidJUnit4.class)
public class ScreenshotsTest {

    private static final String SCREEN_API_EXAMPLE_APP = "net.gini.android.vision.screenapiexample";

    private UiDevice mDevice;

    @Before
    public void setUp() throws Exception {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        assertThat(mDevice).isNotNull();
        // Start from the home screen
        mDevice.pressHome();
    }

    @Test
    public void screenApiExample_takeScreenshot_fromCameraScreen_andReviewScreen()
            throws InterruptedException, UiObjectNotFoundException {
        grantCameraPermission(SCREEN_API_EXAMPLE_APP);
        openApp(SCREEN_API_EXAMPLE_APP, mDevice);

        MainScreen mainScreen = new MainScreen(mDevice);
        CameraScreen cameraScreen = new CameraScreen(mDevice);
        ReviewScreen reviewScreen = new ReviewScreen(mDevice);

        // Main Screen
        assertThat(mainScreen.isVisible()).named("Main Screen is displayed").isTrue();
        mainScreen.startGiniVisionLibrary();

        // Camera Screen
        assertThat(cameraScreen.isVisible()).named("Camera Screen is displayed").isTrue();
        // Onboarding
        if (cameraScreen.isOnboardingVisible()) {
            cameraScreen.dismissOnboarding();
        }
        takeScreenshot("CameraScreen");
        cameraScreen.triggerCamera();

        // Review Screen
        assertThat(reviewScreen.isVisible()).named("Review Screen is displayed").isTrue();
        takeScreenshot("ReviewScreen");
    }

    private void takeScreenshot(final String name) throws InterruptedException {
        String nameWithTimestamp = name + "_" + new Date().getTime();
        File screenshotFile = ScreenshotHelper.screenshotFileForBitBar(nameWithTimestamp);
        ScreenshotHelper.takeUIAutomatorScreenshot(screenshotFile, mDevice);
    }

}
