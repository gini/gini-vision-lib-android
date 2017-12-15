package net.gini.android.vision.camera;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.OncePerInstallEventStoreHelper.clearOnboardingWasShownPreference;
import static net.gini.android.vision.OncePerInstallEventStoreHelper.setOnboardingWasShownPreference;
import static net.gini.android.vision.test.EspressoMatchers.hasComponent;
import static net.gini.android.vision.test.Helpers.isTablet;
import static net.gini.android.vision.test.Helpers.loadAsset;
import static net.gini.android.vision.test.Helpers.prepareLooper;
import static net.gini.android.vision.test.Helpers.resetDeviceOrientation;
import static net.gini.android.vision.test.Helpers.waitForWindowUpdate;

import static org.junit.Assume.assumeTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.intent.matcher.IntentMatchers;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.filters.RequiresDevice;
import android.support.test.filters.SdkSuppress;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.view.Surface;
import android.view.View;

import net.gini.android.vision.DocumentImportEnabledFileTypes;
import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.PaymentData;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivityTestSpy;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.internal.camera.api.CameraControllerFake;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.review.ReviewActivity;
import net.gini.android.vision.review.ReviewActivityTestSpy;
import net.gini.android.vision.test.EspressoAssertions;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CameraScreenTest {

    private static final int PAUSE_DURATION = 500;

    private static final long CLOSE_CAMERA_PAUSE_DURATION = 1000;
    private static final long TAKE_PICTURE_PAUSE_DURATION = 4000;

    @Rule
    public IntentsTestRule<CameraActivity> mCameraActivityIntentsTestRule = new IntentsTestRule<>(
            CameraActivity.class, true, false);
    @Rule
    public ActivityTestRule<CameraActivityFake> mCameraActivityFakeActivityTestRule =
            new ActivityTestRule<>(
                    CameraActivityFake.class, true, false);

    @Before
    public void setup() throws Exception {
        prepareLooper();
    }

    @After
    public void teardown() throws Exception {
        clearOnboardingWasShownPreference();
        // Wait a little for the camera to close
        Thread.sleep(CLOSE_CAMERA_PAUSE_DURATION);
        resetDeviceOrientation();
    }

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenReviewActivityClass_wasNotGiven() {
        final CameraActivity cameraActivity = new CameraActivity();

        final Intent intent = new Intent(Intent.ACTION_MAIN);
        CameraActivity.setAnalysisActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                AnalysisActivityTestSpy.class);
        cameraActivity.setIntent(intent);

        cameraActivity.readExtras();
    }

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenAnalysisActivityClass_wasNotGiven() {
        final CameraActivity cameraActivity = new CameraActivity();

        final Intent intent = new Intent(Intent.ACTION_MAIN);
        CameraActivity.setReviewActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                ReviewActivityTestSpy.class);
        cameraActivity.setIntent(intent);

        cameraActivity.readExtras();
    }

    @Test
    public void should_showOnboarding_onFirstLaunch_ifNotDisabled() {
        final Intent intent = getCameraActivityIntent();
        mCameraActivityIntentsTestRule.launchActivity(intent);

        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @NonNull
    private Intent getCameraActivityIntent() {
        return getCameraActivityIntent(CameraActivity.class, null);
    }

    @NonNull
    private <T extends CameraActivity> Intent getCameraActivityIntent(
            @NonNull final Class<T> cameraActivityClass,
            @Nullable final GiniVisionFeatureConfiguration featureConfiguration) {
        final Intent intent = new Intent(InstrumentationRegistry.getTargetContext(),
                cameraActivityClass);
        CameraActivity.setReviewActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                ReviewActivityTestSpy.class);
        CameraActivity.setAnalysisActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                AnalysisActivityTestSpy.class);
        if (featureConfiguration != null) {
            intent.putExtra(CameraActivity.EXTRA_IN_GINI_VISION_FEATURE_CONFIGURATION,
                    featureConfiguration);
        }
        return intent;
    }

    @Test
    public void should_notShowOnboarding_onFirstLaunch_ifDisabled() {
        startCameraActivityWithoutOnboarding();

        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.doesNotExist());
    }

    @NonNull
    private CameraActivity startCameraActivityWithoutOnboarding() {
        final Intent intent = getCameraActivityIntent();
        intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);
        return mCameraActivityIntentsTestRule.launchActivity(intent);
    }

    @NonNull
    private CameraActivityFake startCameraActivityFakeWithoutOnboarding(
            @Nullable final GiniVisionFeatureConfiguration featureConfiguration) {
        final Intent intent = getCameraActivityIntent(CameraActivityFake.class,
                featureConfiguration);
        intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);
        return mCameraActivityFakeActivityTestRule.launchActivity(intent);
    }

    @Test
    public void should_showOnboarding_ifRequested_andWasAlreadyShownOnFirstLaunch() {
        setOnboardingWasShownPreference();

        final Intent intent = getCameraActivityIntent();
        intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING, true);
        mCameraActivityIntentsTestRule.launchActivity(intent);

        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void should_passCustomOnboardingPages_toOnboardingActivity()
            throws Exception {
        final ArrayList<OnboardingPage> onboardingPages = new ArrayList<>(1);
        onboardingPages.add(
                new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align));

        final Intent intent = getCameraActivityIntent();
        intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);
        intent.putExtra(CameraActivity.EXTRA_IN_ONBOARDING_PAGES, onboardingPages);
        final CameraActivity cameraActivity = mCameraActivityIntentsTestRule.launchActivity(intent);

        Thread.sleep(PAUSE_DURATION);

        cameraActivity.startOnboardingActivity();

        Intents.intended(IntentMatchers.hasComponent(OnboardingActivity.class.getName()));
        Intents.intended(
                IntentMatchers.hasExtra(Matchers.equalTo(OnboardingActivity.EXTRA_ONBOARDING_PAGES),
                        Matchers.any(ArrayList.class)));
    }

    @Test
    public void should_showOnboarding_whenOnboardingMenuItem_wasTapped() {
        final CameraActivity cameraActivity = startCameraActivityWithoutOnboarding();

        cameraActivity.startOnboardingActivity();

        Intents.intended(IntentMatchers.hasComponent(OnboardingActivity.class.getName()));
    }

    @RequiresDevice
    @SdkSuppress(minSdkVersion = 23)
    @Test
    public void a_should_showNoPermissionView_ifNoCameraPermission() {
        // Gini Vision Library does not handle runtime permissions and the no permission view is
        // shown by default
        startCameraActivityWithoutOnboarding();

        Espresso.onView(ViewMatchers.withId(R.id.gv_layout_camera_no_permission))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @RequiresDevice
    @SdkSuppress(minSdkVersion = 23)
    @Test
    public void b_should_showCameraPreview_afterCameraPermission_wasGranted()
            throws UiObjectNotFoundException {
        startCameraActivityWithoutOnboarding();

        final UiDevice uiDevice = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());

        // Open the Application Details in the Settings
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_camera_no_permission))
                .perform(ViewActions.click());

        // Open the Permissions settings
        final UiObject permissionsItem = uiDevice.findObject(new UiSelector().text("Permissions"));
        permissionsItem.clickAndWaitForNewWindow();

        // Grant Camera permission
        final UiObject cameraItem = uiDevice.findObject(new UiSelector().text("Camera"));
        if (!cameraItem.isChecked()) {
            cameraItem.click();
        }

        // Go back to our test app
        uiDevice.pressBack();
        uiDevice.pressBack();

        // Verifiy that the no permission view was removed
        Espresso.onView(ViewMatchers.withId(R.id.gv_layout_camera_no_permission))
                .check(ViewAssertions.matches(
                        ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)));

        // Verify that the camera preview is visible
        Espresso.onView(ViewMatchers.withId(R.id.gv_camera_preview))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @RequiresDevice
    @Test
    public void should_showReviewScreen_afterPictureWasTaken() throws InterruptedException {
        startCameraActivityWithoutOnboarding();

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_camera_trigger))
                .perform(ViewActions.click());

        // Give some time for the camera to take a picture
        Thread.sleep(TAKE_PICTURE_PAUSE_DURATION);

        Intents.intended(IntentMatchers.hasComponent(ReviewActivityTestSpy.class.getName()));
    }

    @RequiresDevice
    @Test
    public void should_takeOnlyOnePicture_ifTrigger_wasPressedMultipleTimes()
            throws InterruptedException {
        startCameraActivityWithoutOnboarding();

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_camera_trigger))
                .perform(ViewActions.doubleClick());

        // Give some time for the camera to take a picture
        Thread.sleep(TAKE_PICTURE_PAUSE_DURATION);

        Intents.intended(IntentMatchers.hasComponent(ReviewActivityTestSpy.class.getName()));
    }

    @RequiresDevice
    @Test
    public void should_passAnalysisActivityIntent_toReviewActivity() throws InterruptedException {
        startCameraActivityWithoutOnboarding();

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_camera_trigger))
                .perform(ViewActions.click());

        // Give some time for the camera to take a picture
        Thread.sleep(TAKE_PICTURE_PAUSE_DURATION);

        Intents.intended(IntentMatchers.hasComponent(ReviewActivityTestSpy.class.getName()));
        Intents.intended(
                IntentMatchers.hasExtra(Matchers.equalTo(ReviewActivity.EXTRA_IN_ANALYSIS_ACTIVITY),
                        hasComponent(AnalysisActivityTestSpy.class.getName())));
    }

    @Test
    public void
    should_notFinish_whenReceivingActivityResult_withResultCodeCancelled_fromReviewActivity() {
        final CameraActivity cameraActivitySpy = Mockito.spy(new CameraActivity());

        cameraActivitySpy.onActivityResult(CameraActivity.REVIEW_DOCUMENT_REQUEST,
                Activity.RESULT_CANCELED, new Intent());

        verify(cameraActivitySpy, never()).finish();
    }

    @Test
    public void
    should_finishIfEnabledByClient_whenReceivingActivityResult_withResultCodeCancelled_fromReviewActivity() {
        final Intent intentAllowBackButtonToClose = getCameraActivityIntent();
        intentAllowBackButtonToClose.putExtra(
                CameraActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, true);

        final CameraActivity cameraActivity = new CameraActivity();
        cameraActivity.setIntent(intentAllowBackButtonToClose);
        cameraActivity.readExtras();

        final CameraActivity cameraActivitySpy = Mockito.spy(cameraActivity);

        cameraActivitySpy.onActivityResult(CameraActivity.REVIEW_DOCUMENT_REQUEST,
                Activity.RESULT_CANCELED, new Intent());

        verify(cameraActivitySpy).finish();
    }

    @Test
    public void should_passBackButtonClosesLibraryExtra_toReviewActivity()
            throws InterruptedException {
        final CameraActivity cameraActivity = startCameraActivityWithBackButtonShouldCloseLibrary();

        final CameraActivity cameraActivitySpy = Mockito.spy(cameraActivity);
        // Prevent really starting the ReviewActivity
        doNothing().when(cameraActivitySpy).startActivityForResult(any(Intent.class), anyInt());
        // Fake taking of a picture, which will cause the ReviewActivity to be launched
        cameraActivitySpy.onDocumentAvailable(DocumentFactory.newDocumentFromPhoto(
                PhotoFactory.newPhotoFromJpeg(new byte[]{}, 0, "portrait", "phone", "camera")));

        // Check that the extra was passed on to the ReviewActivity
        verify(cameraActivitySpy).startActivityForResult(argThat(
                intentWithExtraBackButtonShouldCloseLibrary()), anyInt());
    }

    @RequiresDevice
    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void should_adaptCameraPreviewSize_toLandscapeOrientation_onTablets() throws Exception {
        // Given
        assumeTrue(isTablet());

        final UiDevice uiDevice = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());
        uiDevice.setOrientationNatural();
        waitForWindowUpdate(uiDevice);

        final CameraActivity cameraActivity = startCameraActivityWithoutOnboarding();
        final View cameraPreview = cameraActivity.findViewById(R.id.gv_camera_preview);
        final int initialWidth = cameraPreview.getWidth();
        final int initialHeight = cameraPreview.getHeight();

        // When
        uiDevice.setOrientationRight();
        waitForWindowUpdate(uiDevice);

        // Then
        // Preview should have the reverse aspect ratio
        Espresso.onView(
                ViewMatchers.withId(R.id.gv_camera_preview)).check(
                EspressoAssertions.hasSizeRatio((float) initialHeight / initialWidth));
    }

    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void should_forcePortraitOrientation_onPhones() throws Exception {
        // Given
        assumeTrue(!isTablet());

        final UiDevice uiDevice = UiDevice.getInstance(
                InstrumentationRegistry.getInstrumentation());
        uiDevice.setOrientationLeft();
        waitForWindowUpdate(uiDevice);

        final CameraActivity cameraActivity = startCameraActivityWithoutOnboarding();
        waitForWindowUpdate(uiDevice);

        // Then
        final int rotation = cameraActivity.getWindowManager().getDefaultDisplay().getRotation();
        assertThat(rotation)
                .isEqualTo(Surface.ROTATION_0);
    }

    @NonNull
    private CameraActivity startCameraActivityWithBackButtonShouldCloseLibrary() {
        final Intent intentAllowBackButtonToClose = getCameraActivityIntent();
        intentAllowBackButtonToClose.putExtra(
                CameraActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, true);

        final CameraActivity cameraActivity = new CameraActivity();
        cameraActivity.setIntent(intentAllowBackButtonToClose);
        cameraActivity.readExtras();
        return cameraActivity;
    }

    @NonNull
    private ArgumentMatcher<Intent> intentWithExtraBackButtonShouldCloseLibrary() {
        return new ArgumentMatcher<Intent>() {
            @Override
            public boolean matches(final Intent intent) {
                final boolean shouldCloseLibrary = intent.getBooleanExtra(
                        ReviewActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, false);
                return shouldCloseLibrary;
            }

            @Override
            public String toString() {
                return "Intent { EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY=true }";
            }
        };
    }

    @Test
    public void should_detectBezahlCode_andShowPopup_andReturnPaymentData_whenPopupClicked()
            throws IOException, InterruptedException {
        detectAndCheckQRCode("qrcode_bezahlcode.jpeg", "qrcode_bezahlcode_nv21.bmp",
                new PaymentData("GINI GMBH",
                        "BezahlCode Test",
                        "DE27100777770209299700", "DEUTDEMMXXX",
                        "140.40:EUR"));
    }

    private void detectAndCheckQRCode(@NonNull final String jpegFilename,
            @NonNull final String nv21Filename, @NonNull final PaymentData paymentData)
            throws IOException, InterruptedException {
        // Given
        assumeTrue(!isTablet());

        final GiniVisionFeatureConfiguration featureConfiguration = GiniVisionFeatureConfiguration
                .buildNewConfiguration()
                .setQRCodeScanningEnabled(true)
                .build();

        final CameraActivityFake cameraActivityFake = startCameraActivityFakeWithoutOnboarding(
                featureConfiguration);

        detectQRCode(cameraActivityFake, jpegFilename, nv21Filename);

        // When
        Thread.sleep(PAUSE_DURATION);
        Espresso.onView(ViewMatchers.withId(R.id.gv_payment_data_detected_popup_container))
                .perform(ViewActions.click());

        // Then
        assertThat(mCameraActivityFakeActivityTestRule.getActivity().getPaymentData())
                .isEqualTo(paymentData);
    }

    private void detectQRCode(
            final CameraActivityFake cameraActivityFake,
            @NonNull final String jpegFilename,
            @NonNull final String nv21Filename)
            throws IOException {
        final CameraControllerFake cameraControllerFake =
                cameraActivityFake.getCameraControllerFake();
        assertThat(cameraControllerFake.getPreviewCallback()).isNotNull();
        cameraControllerFake.showImageAsPreview(loadAsset(jpegFilename), loadAsset(nv21Filename));
    }

    @Test
    public void should_detectEPC069_andShowPopup_andReturnPaymentData_whenPopupClicked()
            throws IOException, InterruptedException {
        detectAndCheckQRCode("qrcode_epc069_12.jpeg", "qrcode_epc069_12_nv21.bmp",
                new PaymentData("Girosolution GmbH",
                        "BezahlCode Test",
                        "DE19690516200000581900", "SOLADES1PFD",
                        "140.40:EUR"));
    }

    @Test
    public void should_hidePaymentDataDetectedPopup_afterSomeDelay()
            throws IOException, InterruptedException {
        // Given
        assumeTrue(!isTablet());

        final GiniVisionFeatureConfiguration featureConfiguration = GiniVisionFeatureConfiguration
                .buildNewConfiguration()
                .setQRCodeScanningEnabled(true)
                .build();

        final CameraActivityFake cameraActivityFake = startCameraActivityFakeWithoutOnboarding(
                featureConfiguration);

        cameraActivityFake.getCameraFragmentImplFake().setHidePaymentDataDetectedPopupDelayMs(100);

        detectQRCode(cameraActivityFake, "qrcode_bezahlcode.jpeg", "qrcode_bezahlcode_nv21.bmp");

        // When
        final long hideDelay =
                mCameraActivityFakeActivityTestRule.getActivity().getCameraFragmentImplFake().getHidePaymentDataDetectedPopupDelayMs();
        Thread.sleep(hideDelay + CameraFragmentImpl.DEFAULT_ANIMATION_DURATION + 200);
        Espresso.onView(ViewMatchers.withId(R.id.gv_payment_data_detected_popup_container))
                .check(ViewAssertions.matches(ViewMatchers.withAlpha(0)));
    }

    @Test
    public void should_hideAndShowPaymentDataDetectedPopup_whenNewPaymentData_wasDetected()
            throws IOException, InterruptedException {
        // Given
        assumeTrue(!isTablet());

        final GiniVisionFeatureConfiguration featureConfiguration = GiniVisionFeatureConfiguration
                .buildNewConfiguration()
                .setQRCodeScanningEnabled(true)
                .build();

        final CameraActivityFake cameraActivityFake = startCameraActivityFakeWithoutOnboarding(
                featureConfiguration);

        // When
        detectQRCode(cameraActivityFake, "qrcode_bezahlcode.jpeg", "qrcode_bezahlcode_nv21.bmp");

        detectQRCode(cameraActivityFake, "qrcode_epc069_12.jpeg", "qrcode_epc069_12_nv21.bmp");

        // Then
        final CameraFragmentImpl cameraFragmentImplFake =
                cameraActivityFake.getCameraFragmentImplFake();
        Thread.sleep(CameraFragmentImpl.DEFAULT_ANIMATION_DURATION + 100);
        Mockito.verify(cameraFragmentImplFake, times(2))
                .showPaymentDataDetectedPopup(anyLong());
    }

    @Test
    public void should_notShowPaymentDataDetectedPopup_whenInterfaceIsHidden()
            throws IOException, InterruptedException {
        // Given
        assumeTrue(!isTablet());

        final GiniVisionFeatureConfiguration featureConfiguration = GiniVisionFeatureConfiguration
                .buildNewConfiguration()
                .setQRCodeScanningEnabled(true)
                .build();

        final CameraActivityFake cameraActivityFake = startCameraActivityFakeWithoutOnboarding(
                featureConfiguration);
        cameraActivityFake.getCameraFragmentImplFake().hideInterface();

        // When
        detectQRCode(cameraActivityFake, "qrcode_bezahlcode.jpeg", "qrcode_bezahlcode_nv21.bmp");

        // Then
        Thread.sleep(CameraFragmentImpl.DEFAULT_ANIMATION_DURATION + 100);
        Espresso.onView(ViewMatchers.withId(R.id.gv_payment_data_detected_popup_container))
                .check(ViewAssertions.matches(ViewMatchers.withAlpha(0)));
    }

    @Test
    public void should_notShowPaymentDataDetectedPopup_whenDocumentUploadHint_isShown()
            throws Throwable {
        // Given
        assumeTrue(!isTablet());

        final GiniVisionFeatureConfiguration featureConfiguration = GiniVisionFeatureConfiguration
                .buildNewConfiguration()
                .setQRCodeScanningEnabled(true)
                .setDocumentImportEnabledFileTypes(DocumentImportEnabledFileTypes.PDF_AND_IMAGES)
                .build();

        final CameraActivityFake cameraActivityFake = startCameraActivityFakeWithoutOnboarding(
                featureConfiguration);

        mCameraActivityFakeActivityTestRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cameraActivityFake.getCameraFragmentImplFake().showUploadHintPopUp();
            }
        });

        // When
        detectQRCode(cameraActivityFake, "qrcode_bezahlcode.jpeg", "qrcode_bezahlcode_nv21.bmp");

        // Then
        Thread.sleep(CameraFragmentImpl.DEFAULT_ANIMATION_DURATION + 100);
        Espresso.onView(ViewMatchers.withId(R.id.gv_payment_data_detected_popup_container))
                .check(ViewAssertions.matches(ViewMatchers.withAlpha(0)));
    }
}