package net.gini.android.vision.camera;

import static net.gini.android.vision.OncePerInstallEventStoreHelper.clearOnboardingWasShownPreference;
import static net.gini.android.vision.OncePerInstallEventStoreHelper.setOnboardingWasShownPreference;
import static net.gini.android.vision.test.EspressoMatchers.hasComponent;
import static net.gini.android.vision.test.Helpers.prepareLooper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.analysis.AnalysisActivityTestStub;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.onboarding.OnboardingActivity;
import net.gini.android.vision.onboarding.OnboardingPage;
import net.gini.android.vision.review.ReviewActivity;
import net.gini.android.vision.review.ReviewActivityTestStub;

import org.hamcrest.Description;
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

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CameraScreenTest {

    private static final long CLOSE_CAMERA_PAUSE_DURATION = 1000;
    private static final long TAKE_PICTURE_PAUSE_DURATION = 4000;

    @Rule
    public IntentsTestRule<CameraActivity> mIntentsTestRule = new IntentsTestRule<>(
            CameraActivity.class, true, false);

    private UiDevice mUiDevice;

    @Before
    public void setup() {
        prepareLooper();
        mUiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @After
    public void teardown() throws InterruptedException {
        clearOnboardingWasShownPreference();
        // Wait a little for the camera to close
        Thread.sleep(CLOSE_CAMERA_PAUSE_DURATION);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenReviewActivityClass_wasNotGiven() {
        CameraActivity cameraActivity = new CameraActivity();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        CameraActivity.setAnalysisActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                AnalysisActivityTestStub.class);
        cameraActivity.setIntent(intent);

        cameraActivity.readExtras();
    }

    @Test(expected = IllegalStateException.class)
    public void should_throwException_whenAnalysisActivityClass_wasNotGiven() {
        CameraActivity cameraActivity = new CameraActivity();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        CameraActivity.setReviewActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                ReviewActivityTestStub.class);
        cameraActivity.setIntent(intent);

        cameraActivity.readExtras();
    }

    @Test
    public void should_showOnboarding_onFirstLaunch_ifNotDisabled() {
        Intent intent = getCameraActivityIntent();
        mIntentsTestRule.launchActivity(intent);

        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @NonNull
    private Intent getCameraActivityIntent() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        CameraActivity.setReviewActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                ReviewActivityTestStub.class);
        CameraActivity.setAnalysisActivityExtra(intent, InstrumentationRegistry.getTargetContext(),
                AnalysisActivityTestStub.class);
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
        Intent intent = getCameraActivityIntent();
        intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);
        return mIntentsTestRule.launchActivity(intent);
    }

    @Test
    public void should_showOnboarding_ifRequested_andWasAlreadyShownOnFirstLaunch() {
        setOnboardingWasShownPreference();

        Intent intent = getCameraActivityIntent();
        intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING, true);
        mIntentsTestRule.launchActivity(intent);

        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void should_passCustomOnboardingPages_toOnboardingActivity() {
        ArrayList<OnboardingPage> onboardingPages = new ArrayList<>(1);
        onboardingPages.add(
                new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align));

        Intent intent = getCameraActivityIntent();
        intent.putExtra(CameraActivity.EXTRA_IN_SHOW_ONBOARDING_AT_FIRST_RUN, false);
        intent.putExtra(CameraActivity.EXTRA_IN_ONBOARDING_PAGES, onboardingPages);
        mIntentsTestRule.launchActivity(intent);

        InstrumentationRegistry.getInstrumentation().waitForIdleSync();

        // Starting by clicking the menu item, otherwise the intent is not recorded ...
        Espresso.onView(ViewMatchers.withId(R.id.gv_action_show_onboarding))
                .perform(ViewActions.click());

        Intents.intended(IntentMatchers.hasComponent(OnboardingActivity.class.getName()));
        Intents.intended(
                IntentMatchers.hasExtra(Matchers.equalTo(OnboardingActivity.EXTRA_ONBOARDING_PAGES),
                        Matchers.any(ArrayList.class)));
    }

    @Test
    public void should_showOnboarding_whenOnboardingMenuItem_wasTapped() {
        startCameraActivityWithoutOnboarding();

        Espresso.onView(ViewMatchers.withId(R.id.gv_action_show_onboarding))
                .perform(ViewActions.click());

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

        // Open the Application Details in the Settings
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_camera_no_permission))
                .perform(ViewActions.click());

        // Open the Permissions settings
        UiObject permissionsItem = mUiDevice.findObject(new UiSelector().text("Permissions"));
        permissionsItem.clickAndWaitForNewWindow();

        // Grant Camera permission
        UiObject cameraItem = mUiDevice.findObject(new UiSelector().text("Camera"));
        if (!cameraItem.isChecked()) {
            cameraItem.click();
        }

        // Go back to our test app
        mUiDevice.pressBack();
        mUiDevice.pressBack();

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

        Intents.intended(IntentMatchers.hasComponent(ReviewActivityTestStub.class.getName()));
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

        Intents.intended(IntentMatchers.hasComponent(ReviewActivityTestStub.class.getName()));
    }

    @RequiresDevice
    @Test
    public void should_passAnalysisActivityIntent_toReviewActivity() throws InterruptedException {
        startCameraActivityWithoutOnboarding();

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_camera_trigger))
                .perform(ViewActions.click());

        // Give some time for the camera to take a picture
        Thread.sleep(TAKE_PICTURE_PAUSE_DURATION);

        Intents.intended(IntentMatchers.hasComponent(ReviewActivityTestStub.class.getName()));
        Intents.intended(
                IntentMatchers.hasExtra(Matchers.equalTo(ReviewActivity.EXTRA_IN_ANALYSIS_ACTIVITY),
                        hasComponent(AnalysisActivityTestStub.class.getName())));
    }

    @Test
    public void should_notFinish_whenReceivingActivityResult_withResultCodeCancelled_fromReviewActivity() {
        final CameraActivity cameraActivitySpy = Mockito.spy(new CameraActivity());

        cameraActivitySpy.onActivityResult(CameraActivity.REVIEW_DOCUMENT_REQUEST,
                Activity.RESULT_CANCELED, new Intent());

        verify(cameraActivitySpy, never()).finish();
    }

    @Test
    public void should_finishIfEnabledByClient_whenReceivingActivityResult_withResultCodeCancelled_fromReviewActivity() {
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
        cameraActivitySpy.onDocumentAvailable(Document.fromPhoto(Photo.fromJpeg(new byte[]{}, 0)));

        // Check that the extra was passed on to the ReviewActivity
        verify(cameraActivitySpy).startActivityForResult(argThat(
                intentWithExtraBackButtonShouldCloseLibrary()), anyInt());
    }

    @NonNull
    private CameraActivity startCameraActivityWithBackButtonShouldCloseLibrary() {
        final Intent intentAllowBackButtonToClose = getCameraActivityIntent();
        intentAllowBackButtonToClose.putExtra(
                CameraActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, true);

        CameraActivity cameraActivity = new CameraActivity();
        cameraActivity.setIntent(intentAllowBackButtonToClose);
        cameraActivity.readExtras();
        return cameraActivity;
    }

    @NonNull
    private ArgumentMatcher<Intent> intentWithExtraBackButtonShouldCloseLibrary() {
        return new ArgumentMatcher<Intent>() {
            @Override
            public boolean matches(final Object argument) {
                final Intent intent = (Intent) argument;
                //noinspection UnnecessaryLocalVariable
                final boolean shouldCloseLibrary = intent.getBooleanExtra(
                        ReviewActivity.EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY, false);
                return shouldCloseLibrary;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText("Intent { EXTRA_IN_BACK_BUTTON_SHOULD_CLOSE_LIBRARY=true }");
            }
        };
    }

}