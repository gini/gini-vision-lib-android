package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.Helpers.isTablet;
import static net.gini.android.vision.test.Helpers.resetDeviceOrientation;
import static net.gini.android.vision.test.Helpers.waitForWindowUpdate;

import static org.junit.Assume.assumeTrue;

import android.content.Intent;
import android.view.Surface;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.test.EspressoMatchers;

import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;

@RunWith(AndroidJUnit4.class)
public class OnboardingScreenTest {

    private static final long TEST_PAUSE_DURATION = 500;

    @Rule
    public ActivityTestRule<OnboardingActivity> mActivityTestRule = new ActivityTestRule<>(
            OnboardingActivity.class, true, false);
    @Rule
    public ActivityTestRule<OnboardingFragmentHostActivityNotListener>
            mOnboardingFragmentHostActivityNotListenerTR = new ActivityTestRule<>(
            OnboardingFragmentHostActivityNotListener.class, true, false);
    @Rule
    public ActivityTestRule<OnboardingFragmentHostActivity>
            mOnboardingFragmentHostActivityTR = new ActivityTestRule<>(
            OnboardingFragmentHostActivity.class, true, false);

    @After
    public void tearDown() throws Exception {
        OnboardingFragmentHostActivityNotListener.sListener = null;
        resetDeviceOrientation();
        GiniVision.cleanup(ApplicationProvider.getApplicationContext());
    }

    @Test
    public void should_showCustomPages_whenSet() throws InterruptedException {
        final ArrayList<OnboardingPage> customPages = new ArrayList<>(1);
        customPages.add(new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger));
        customPages.add(
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate));

        final Intent intent = getOnboardingActivityIntent();
        intent.putExtra(OnboardingActivity.EXTRA_ONBOARDING_PAGES, customPages);
        final OnboardingActivity activity = startOnboardingActivity(intent);

        // Give some time for the activity to start
        Thread.sleep(TEST_PAUSE_DURATION);

        // Verify the first page
        Espresso.onView(ViewMatchers.withText(R.string.gv_title_camera))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Go to the second page
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Give some time for paging animation to finish
        Thread.sleep(TEST_PAUSE_DURATION);

        // Verify the second page
        Espresso.onView(ViewMatchers.withText(R.string.gv_title_review))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Click the next button again to finish
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Give some time for paging animation to finish
        Thread.sleep(TEST_PAUSE_DURATION);

        assertThat(activity.isFinishing()).isTrue();
    }

    @Test
    public void should_showCustomPages_whenSetUsingGiniVision() throws InterruptedException {
        final ArrayList<OnboardingPage> customPages = new ArrayList<>(1);
        customPages.add(new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger));
        customPages.add(
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate));

        GiniVision.newInstance()
                .setCustomOnboardingPages(customPages)
                .build();

        final Intent intent = getOnboardingActivityIntent();
        final OnboardingActivity activity = startOnboardingActivity(intent);

        // Give some time for the activity to start
        Thread.sleep(TEST_PAUSE_DURATION);

        // Verify the first page
        Espresso.onView(ViewMatchers.withText(R.string.gv_title_camera))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Go to the second page
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Give some time for paging animation to finish
        Thread.sleep(TEST_PAUSE_DURATION);

        // Verify the second page
        Espresso.onView(ViewMatchers.withText(R.string.gv_title_review))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Click the next button again to finish
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Give some time for paging animation to finish
        Thread.sleep(TEST_PAUSE_DURATION);

        assertThat(activity.isFinishing()).isTrue();
    }

    @Test
    public void should_showEmptyLastPage_byDefault() {
        startOnboardingActivity();

        // ViewPager should contain the default pages and an empty last page
        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.matches(EspressoMatchers.hasPageCount(
                        getDefaultPages().length + 1)));
    }

    @Test
    @SdkSuppress(minSdkVersion = 18)
    public void should_forcePortraitOrientation_onPhones() throws Exception {
        // Given
        assumeTrue(!isTablet());

        final UiDevice uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        uiDevice.setOrientationLeft();
        waitForWindowUpdate(uiDevice);

        final OnboardingActivity onboardingActivity = startOnboardingActivity();
        waitForWindowUpdate(uiDevice);

        // Then
        final int rotation = onboardingActivity.getWindowManager().getDefaultDisplay().getRotation();
        assertThat(rotation)
                .isEqualTo(Surface.ROTATION_0);
    }

    private Enum[] getDefaultPages() {
        return isTablet() ? DefaultPagesTablet.values() : DefaultPagesPhone.values();
    }

    private OnboardingActivity startOnboardingActivity() {
        return startOnboardingActivity(null);
    }

    private OnboardingActivity startOnboardingActivity(@Nullable Intent intent) {
        if (intent == null) {
            intent = getOnboardingActivityIntent();
        }
        return mActivityTestRule.launchActivity(intent);
    }

    @NonNull
    private Intent getOnboardingActivityIntent() {
        return new Intent(ApplicationProvider.getApplicationContext(), OnboardingActivity.class);
    }

    @Test
    public void should_useExplicitListener_whenActivity_isNotListener() throws Exception {
        // Given
        final AtomicBoolean isClosed = new AtomicBoolean();
        OnboardingFragmentHostActivityNotListener.sListener = new OnboardingFragmentListener() {
            @Override
            public void onCloseOnboarding() {
                isClosed.set(true);
            }

            @Override
            public void onError(@NonNull final GiniVisionError error) {

            }
        };
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                OnboardingFragmentHostActivityNotListener.class);
        final OnboardingFragmentHostActivityNotListener activity =
                mOnboardingFragmentHostActivityNotListenerTR.launchActivity(intent);
        // When
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getFragment().mFragmentImpl.mButtonNext.performClick();
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        // Then
        assertThat(isClosed.get()).isTrue();
    }

    @Test
    public void should_useActivity_asListener_whenAvailable() throws Exception {
        // Given
        final Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                OnboardingFragmentHostActivity.class);
        final OnboardingFragmentHostActivity activity =
                mOnboardingFragmentHostActivityTR.launchActivity(intent);
        // When
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.getFragment().mFragmentImpl.mButtonNext.performClick();
            }
        });
        InstrumentationRegistry.getInstrumentation().waitForIdleSync();
        // Then
        assertThat(activity.isClosed()).isTrue();
    }
}
