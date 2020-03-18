package net.gini.android.vision.onboarding;

import static android.os.Looper.getMainLooper;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.test.ViewAtIndexMatcher.withIndex;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.robolectric.Shadows.shadowOf;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;

import com.google.common.collect.Lists;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.test.FragmentImplFactory;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.LooperMode;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
@RunWith(AndroidJUnit4.class)
@LooperMode(LooperMode.Mode.PAUSED)
public class OnboardingFragmentImplTest {

    @After
    public void tearDown() throws Exception {
        OnboardingFragmentCompatFake.sFragmentImplFactory = null;
    }

    @Test
    public void should_passShowEmptyLastPage_toPresenter() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final boolean showEmptyLastPage = true;

        // When
        launchHostActivity(presenter, onboardingFragmentImplRef, showEmptyLastPage, null);

        // Then
        verify(presenter).addEmptyLastPage();
    }

    @NonNull
    private ActivityScenario<OnboardingFragmentHostActivity> launchHostActivity(
            @NonNull final OnboardingScreenPresenter presenter,
            @NonNull final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef,
            final boolean showEmptyLastPage,
            @Nullable final ArrayList<OnboardingPage> pages) {
        OnboardingFragmentCompatFake.sFragmentImplFactory =
                new FragmentImplFactory<OnboardingFragmentImpl, OnboardingFragmentCompat>() {
                    @NonNull
                    @Override
                    public OnboardingFragmentImpl createFragmentImpl(
                            @NonNull final OnboardingFragmentCompat fragment) {
                        final OnboardingFragmentImpl onboardingFragmentImpl =
                                new OnboardingFragmentImpl(fragment, showEmptyLastPage, pages) {

                                    @Override
                                    void createPresenter(@NonNull final Activity activity) {
                                        setPresenter(presenter);
                                    }

                                };
                        onboardingFragmentImplRef.set(onboardingFragmentImpl);
                        return onboardingFragmentImpl;
                    }
                };
        return ActivityScenario.launch(OnboardingFragmentHostActivity.class);
    }

    @Test
    public void should_passCustomPages_toPresenter() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> customPages = Lists.newArrayList(
                DefaultPagesPhone.ALIGN.getPage(),
                DefaultPagesPhone.FLAT.getPage());

        // When
        launchHostActivity(
                presenter,
                onboardingFragmentImplRef, true,
                customPages);

        // Then
        verify(presenter).setCustomPages(customPages);
    }

    @Test
    public void should_showPages() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();

        final ActivityScenario<OnboardingFragmentHostActivity>
                scenario = launchHostActivity(presenter,
                onboardingFragmentImplRef, false, pages);

        // When
        scenario.onActivity(new ActivityScenario.ActivityAction<OnboardingFragmentHostActivity>() {
            @Override
            public void perform(final OnboardingFragmentHostActivity activity) {
                final OnboardingFragmentImpl onboardingFragmentImpl =
                        onboardingFragmentImplRef.get();
                onboardingFragmentImpl.showPages(pages, false);
            }
        });

        // Then
        onView(withText(pages.get(0).getTextResId())).check(
                matches(isCompletelyDisplayed()));
    }

    @Test
    public void should_scrollToPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();

        final ActivityScenario<OnboardingFragmentHostActivity>
                scenario = launchHostActivity(presenter,
                onboardingFragmentImplRef, false, pages);

        // When
        scenario.onActivity(new ActivityScenario.ActivityAction<OnboardingFragmentHostActivity>() {
            @Override
            public void perform(final OnboardingFragmentHostActivity activity) {
                final OnboardingFragmentImpl onboardingFragmentImpl =
                        onboardingFragmentImplRef.get();
                onboardingFragmentImpl.showPages(pages, false);
                onboardingFragmentImpl.scrollToPage(2);
            }
        });

        // Then
        onView(withText(pages.get(2).getTextResId())).check(
                matches(isCompletelyDisplayed()));
    }

    @Test
    public void should_notifyPresenter_whenScrolledToPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();

        final ActivityScenario<OnboardingFragmentHostActivity>
                scenario = launchHostActivity(presenter,
                onboardingFragmentImplRef, false, pages);

        // When
        scenario.onActivity(new ActivityScenario.ActivityAction<OnboardingFragmentHostActivity>() {
            @Override
            public void perform(final OnboardingFragmentHostActivity activity) {
                final OnboardingFragmentImpl onboardingFragmentImpl =
                        onboardingFragmentImplRef.get();
                onboardingFragmentImpl.showPages(pages, false);
                onboardingFragmentImpl.scrollToPage(2);
            }
        });

        // Then
        verify(presenter).onScrolledToPage(2);
    }

    @Test
    public void should_notifyPresenter_whenSwipedToNextPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();

        final ActivityScenario<OnboardingFragmentHostActivity>
                scenario = launchHostActivity(presenter,
                onboardingFragmentImplRef, false, pages);

        // When
        scenario.onActivity(new ActivityScenario.ActivityAction<OnboardingFragmentHostActivity>() {
            @Override
            public void perform(final OnboardingFragmentHostActivity activity) {
                final OnboardingFragmentImpl onboardingFragmentImpl =
                        onboardingFragmentImplRef.get();
                onboardingFragmentImpl.showPages(pages, false);
            }
        });

        onView(withId(R.id.gv_onboarding_viewpager)).perform(ViewActions.swipeLeft());

        // Then
        onView(withText(pages.get(1).getTextResId())).check(matches(isCompletelyDisplayed()));
        verify(presenter).onScrolledToPage(1);
    }

    @Test
    public void should_passListener_toPresenter() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ActivityScenario<OnboardingFragmentHostActivity>
                scenario = launchHostActivity(presenter,
                onboardingFragmentImplRef, false, null);

        final OnboardingFragmentListener listener = new OnboardingFragmentListener() {
            @Override
            public void onCloseOnboarding() {

            }

            @Override
            public void onError(@NonNull final GiniVisionError error) {

            }
        };

        // When
        scenario.onActivity(new ActivityScenario.ActivityAction<OnboardingFragmentHostActivity>() {
            @Override
            public void perform(final OnboardingFragmentHostActivity activity) {
                final OnboardingFragmentImpl onboardingFragmentImpl =
                        onboardingFragmentImplRef.get();
                onboardingFragmentImpl.setListener(listener);
            }
        });

        // Then
        verify(presenter).setListener(listener);
    }

    @Test
    public void should_activatePageIndicator() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> pages = Lists.newArrayList(
                new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger),
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate)
        );

        final ActivityScenario<OnboardingFragmentHostActivity>
                scenario = launchHostActivity(presenter,
                onboardingFragmentImplRef, false, pages);

        // When
        scenario.onActivity(new ActivityScenario.ActivityAction<OnboardingFragmentHostActivity>() {
            @Override
            public void perform(final OnboardingFragmentHostActivity activity) {
                final OnboardingFragmentImpl onboardingFragmentImpl =
                        onboardingFragmentImplRef.get();
                onboardingFragmentImpl.showPages(pages, false);
                onboardingFragmentImpl.activatePageIndicatorForPage(1);
            }
        });

        // Then
        onView(withIndex(0, withTagValue(Matchers.<Object>equalTo("pageIndicator"))))
                .check(matches(withContentDescription("inactive")));
        onView(withIndex(1, withTagValue(Matchers.<Object>equalTo("pageIndicator"))))
                .check(matches(withContentDescription("active")));
    }

    @Test
    public void should_notifyPresenter_whenNextButton_wasClicked() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();

        launchHostActivity(presenter, onboardingFragmentImplRef, false, pages);

        // When
        onView(withId(R.id.gv_button_next)).perform(ViewActions.click());

        // Then
        verify(presenter).showNextPage();
    }

    @Test
    public void should_notShowPageIndicator_forEmptyLastPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = mock(OnboardingScreenPresenter.class);
        final AtomicReference<OnboardingFragmentImpl> onboardingFragmentImplRef =
                new AtomicReference<>();

        final ArrayList<OnboardingPage> pages = DefaultPagesPhone.asArrayList();
        pages.add(new OnboardingPage(0, 0, true));

        final ActivityScenario<OnboardingFragmentHostActivity>
                scenario = launchHostActivity(presenter,
                onboardingFragmentImplRef, true, pages);

        // When
        scenario.onActivity(new ActivityScenario.ActivityAction<OnboardingFragmentHostActivity>() {
            @Override
            public void perform(final OnboardingFragmentHostActivity activity) {
                final OnboardingFragmentImpl onboardingFragmentImpl =
                        onboardingFragmentImplRef.get();
                onboardingFragmentImpl.showPages(pages, true);
            }
        });

        shadowOf(getMainLooper()).idle();

        // Then
        final OnboardingFragmentImpl onboardingFragmentImpl = onboardingFragmentImplRef.get();
        assertThat(onboardingFragmentImpl.getPageIndicators().getPageIndicatorImageViews()).hasSize(
                pages.size() - 1);
    }
}