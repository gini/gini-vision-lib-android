package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import net.gini.android.vision.R;
import net.gini.android.vision.test.EspressoMatchers;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

@RunWith(AndroidJUnit4.class)
public class OnboardingScreenTest {

    private static final long TEST_PAUSE_DURATION = 500;
    @Rule
    public IntentsTestRule<OnboardingActivity> mIntentsTestRule = new IntentsTestRule<>(OnboardingActivity.class, true, false);

    @Test
    public void should_goToNextPage_whenNextButton_isClicked() {
        startOnboardingActivity();

        // Check that we are on the first page
        Espresso.onView(ViewMatchers.withText(DefaultPages.values()[0].getPage().getTextResId()))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click());

        // Check that we are on the second page
        Espresso.onView(ViewMatchers.withText(DefaultPages.values()[1].getPage().getTextResId()))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void should_goToNextPage_whenSwiped() {
        startOnboardingActivity();

        // Check that we are on the first page
        Espresso.onView(ViewMatchers.withText(DefaultPages.values()[0].getPage().getTextResId()))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .perform(ViewActions.swipeLeft());

        // Check that we are on the second page
        Espresso.onView(ViewMatchers.withText(DefaultPages.values()[1].getPage().getTextResId()))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void should_finish_whenNextButton_isClicked_onLastPage() {
        OnboardingActivity activity = startOnboardingActivity();

        // Go to the last page
        Espresso.onView(ViewMatchers.withId(R.id.gv_button_next))
                .perform(ViewActions.click())
                .perform(ViewActions.click())
        // Click the next button on the last page
                .perform(ViewActions.click());

        assertThat(activity.isFinishing()).isTrue();
    }

    @Test
    public void should_finish_whenSwiped_onLastPage() throws InterruptedException {
        OnboardingActivity activity = startOnboardingActivity();

        // Go to the last page by swiping
        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .perform(ViewActions.swipeLeft())
                .perform(ViewActions.swipeLeft())
                // Swipe left on the last page
                .perform(ViewActions.swipeLeft());

        // Wait a little for the animation to finish
        Thread.sleep(TEST_PAUSE_DURATION);

        assertThat(activity.isFinishing()).isTrue();
    }

    @Test
    public void should_showCustomPages_whenSet() throws InterruptedException {
        ArrayList<OnboardingPage> customPages = new ArrayList<>(1);
        customPages.add(new OnboardingPage(R.string.gv_title_camera,R.drawable.gv_camera_trigger));
        customPages.add(new OnboardingPage(R.string.gv_title_review,R.drawable.gv_review_button_rotate));

        Intent intent = getOnboardingActivityIntent();
        intent.putExtra(OnboardingActivity.EXTRA_ONBOARDING_PAGES, customPages);
        OnboardingActivity activity = startOnboardingActivity(intent);

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

        assertThat(activity.isFinishing()).isTrue();
    }

    @Test
    public void should_showEmptyLastPage_byDefault() {
        startOnboardingActivity();

        // ViewPager should contain the default pages and an empty last page
        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.matches(EspressoMatchers.hasPageCount(DefaultPages.values().length + 1)));
    }

    @Test
    public void should_notShowEmptyLastPage_ifRequested() {
        OnboardingActivity onboardingActivity = startOnboardingActivity();
        OnboardingFragmentCompat onboardingFragment = OnboardingFragmentCompat.createInstanceWithoutEmptyLastPage();
        onboardingActivity.showFragment(onboardingFragment);

        // ViewPager should contain the default pages and an empty last page
        Espresso.onView(ViewMatchers.withId(R.id.gv_onboarding_viewpager))
                .check(ViewAssertions.matches(EspressoMatchers.hasPageCount(DefaultPages.values().length)));
    }

    private OnboardingActivity startOnboardingActivity() {
        return startOnboardingActivity(null);
    }

    private OnboardingActivity startOnboardingActivity(@Nullable Intent intent) {
        if (intent == null) {
            intent = getOnboardingActivityIntent();
        }
        return mIntentsTestRule.launchActivity(intent);
    }

    @NonNull
    private Intent getOnboardingActivityIntent() {
        return new Intent(InstrumentationRegistry.getTargetContext(), OnboardingActivity.class);
    }

}
