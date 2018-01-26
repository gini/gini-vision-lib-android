package net.gini.android.vision.screen.screenshots;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.screen.screenshots.Helper.openApp;
import static net.gini.android.vision.screen.screenshots.ScreenshotHelper.screenshotFileForBitBar;
import static net.gini.android.vision.screen.screenshots.ScreenshotHelper.takeUIAutomatorScreenshot;
import static net.gini.android.vision.screen.testhelper.PermissionsHelper.grantCameraPermission;

import android.os.RemoteException;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;

import net.gini.android.vision.screen.screenshots.screens.screenapi.AnalysisScreen;
import net.gini.android.vision.screen.screenshots.screens.screenapi.CameraScreen;
import net.gini.android.vision.screen.screenshots.screens.screenapi.MainScreen;
import net.gini.android.vision.screen.screenshots.screens.screenapi.OnboardingScreen;
import net.gini.android.vision.screen.screenshots.screens.screenapi.ReviewScreen;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

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
    public void screenApiExample_takeScreenshots_naturalOrientation()
            throws InterruptedException, UiObjectNotFoundException, RemoteException {

        grantCameraPermission(SCREEN_API_EXAMPLE_APP);
        openApp(SCREEN_API_EXAMPLE_APP, mDevice);

        mDevice.setOrientationNatural();
        mDevice.waitForWindowUpdate(SCREEN_API_EXAMPLE_APP, 5000);

        takeScreenshots("normal-");
    }

    @Test
    public void screenApiExample_takeScreenshots_rightOrientation()
            throws InterruptedException, UiObjectNotFoundException, RemoteException {
        grantCameraPermission(SCREEN_API_EXAMPLE_APP);
        openApp(SCREEN_API_EXAMPLE_APP, mDevice);

        mDevice.setOrientationLeft();
        mDevice.waitForWindowUpdate(SCREEN_API_EXAMPLE_APP, 5000);

        takeScreenshots("right-");
    }

    private void takeScreenshots(final String namePrefix)
            throws UiObjectNotFoundException, InterruptedException {
        MainScreen mainScreen = new MainScreen(mDevice);
        CameraScreen cameraScreen = new CameraScreen(mDevice);
        ReviewScreen reviewScreen = new ReviewScreen(mDevice);
        AnalysisScreen analysisScreen = new AnalysisScreen(mDevice);

        // Main Screen
        assertThat(mainScreen.isVisible()).named("Main Screen is displayed").isTrue();
        mainScreen.startGiniVisionLibrary();

        // Camera Screen
        assertThat(cameraScreen.isVisible()).named("Camera Screen is displayed").isTrue();

        // Onboarding Screen
        if (cameraScreen.isOnboardingVisible()) {
            takeOnboardingScreenshots(namePrefix);
        } else {
            cameraScreen.showOnboarding();
            takeOnboardingScreenshots(namePrefix);
        }

        // Camera Screen
        takeScreenshot(namePrefix + "CameraScreen");
        cameraScreen.triggerCamera();

        // Review Screen
        assertThat(reviewScreen.isVisible()).named("Review Screen is displayed").isTrue();
        takeScreenshot(namePrefix + "ReviewScreen");

        reviewScreen.proceedToAnalysis();

        // Analysis Screen
        assertThat(analysisScreen.isVisible()).named("Analysis Screen is displayed").isTrue();
        takeScreenshot(namePrefix + "AnalysisScreen");
    }

    private void takeOnboardingScreenshots(final String namePrefix)
            throws InterruptedException, UiObjectNotFoundException {
        OnboardingScreen onboardingScreen = new OnboardingScreen(mDevice);

        assertThat(onboardingScreen.isVisible()).named("Onboarding Screen is displayed").isTrue();
        takeScreenshot(namePrefix + "OnboardingScreen-Page1");

        onboardingScreen.goToNextPage();
        takeScreenshot(namePrefix + "OnboardingScreen-Page2");

        onboardingScreen.goToNextPage();
        takeScreenshot(namePrefix + "OnboardingScreen-Page3");

        onboardingScreen.closeSelf();
    }

    private void takeScreenshot(final String name) throws InterruptedException {
        File screenshotFile = screenshotFileForBitBar(name);
        takeUIAutomatorScreenshot(screenshotFile, mDevice);
    }

}
