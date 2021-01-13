package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Activity;
import android.content.res.Resources;

import com.google.common.collect.Lists;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionHelper;
import net.gini.android.vision.R;
import net.gini.android.vision.tracking.Event;
import net.gini.android.vision.tracking.EventTracker;
import net.gini.android.vision.tracking.OnboardingScreenEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(AndroidJUnit4.class)
public class OnboardingScreenPresenterTest {

    @Mock
    private Activity mActivity;
    @Mock
    private OnboardingScreenContract.View mView;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @After
    public void tearDown() {
        GiniVisionHelper.setGiniVisionInstance(null);
    }

    @Test
    public void should_usePhoneDefaultPages_onPhones() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter(false);

        // Then
        assertThat(presenter.getPages()).containsExactlyElementsIn(
                DefaultPagesPhone.asArrayList()).inOrder();
    }

    @NonNull
    private OnboardingScreenPresenter createPresenter(final boolean isTablet) {
        final Resources resources = mock(Resources.class);
        when(resources.getBoolean(R.bool.gv_is_tablet)).thenReturn(isTablet);
        when(mActivity.getResources()).thenReturn(resources);

        return new OnboardingScreenPresenter(mActivity, mView);
    }

    @Test
    public void should_useTabletDefaultPages_onTablets() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter(true);

        // Then
        assertThat(presenter.getPages()).containsExactlyElementsIn(
                DefaultPagesTablet.asArrayList()).inOrder();
    }

    @Test
    public void should_addEmptyLastPage_toCustomPages_ifRequested() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        // When
        presenter.addEmptyLastPage();

        final List<OnboardingPage> customPages = Lists.newArrayList(
                new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger),
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate)
        );
        presenter.setCustomPages(customPages);

        // Then
        assertThat(presenter.getPages().get(2).getImageResId()).isEqualTo(0);
        assertThat(presenter.getPages().get(2).getTextResId()).isEqualTo(0);
        assertThat(presenter.getPages().get(2).isTransparent()).isTrue();
    }

    @NonNull
    private OnboardingScreenPresenter createPresenter() {
        return createPresenter(false);
    }

    @Test
    public void should_addEmptyLastPage_toCustomPages_ifRequested_afterAddingCustomPages()
            throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        // When
        final List<OnboardingPage> customPages = Lists.newArrayList(
                new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger),
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate)
        );
        presenter.setCustomPages(customPages);

        presenter.addEmptyLastPage();

        // Then
        assertThat(presenter.getPages().get(2).getImageResId()).isEqualTo(0);
        assertThat(presenter.getPages().get(2).getTextResId()).isEqualTo(0);
        assertThat(presenter.getPages().get(2).isTransparent()).isTrue();
    }

    @Test
    public void should_showNextPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        // When
        final List<OnboardingPage> customPages = Lists.newArrayList(
                new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger),
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate)
        );
        presenter.setCustomPages(customPages);

        presenter.showNextPage();

        // Then
        verify(mView).scrollToPage(1);
    }

    @Test
    public void should_notifyListener_toCloseOnboarding_whenShowNextPage_onLastPage_wasRequested()
            throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        final OnboardingFragmentListener listener = mock(OnboardingFragmentListener.class);
        presenter.setListener(listener);

        // When
        final List<OnboardingPage> customPages = Lists.newArrayList(
                new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger),
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate)
        );
        presenter.setCustomPages(customPages);

        presenter.onScrolledToPage(1);
        presenter.showNextPage();

        // Then
        verify(listener).onCloseOnboarding();
    }

    @Test
    public void should_updatePageIndicator_whenScrolledToAPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        // When
        final List<OnboardingPage> customPages = Lists.newArrayList(
                new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger),
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate)
        );
        presenter.setCustomPages(customPages);

        presenter.onScrolledToPage(1);

        // Then
        verify(mView).activatePageIndicatorForPage(1);
    }

    @Test
    public void should_slideOutViews_whenScrolledToTheLastPage_withAnEmptyLastPage()
            throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        final OnboardingFragmentListener listener = mock(OnboardingFragmentListener.class);
        presenter.setListener(listener);

        when(mView.slideOutViews()).thenReturn(CompletableFuture.<Void>completedFuture(null));

        // When
        final List<OnboardingPage> customPages = Lists.newArrayList(
                new OnboardingPage(R.string.gv_title_camera, R.drawable.gv_camera_trigger),
                new OnboardingPage(R.string.gv_title_review, R.drawable.gv_review_button_rotate)
        );
        presenter.setCustomPages(customPages);
        presenter.addEmptyLastPage();

        presenter.onScrolledToPage(2);

        // Then
        verify(mView).slideOutViews();
        verify(listener).onCloseOnboarding();
    }

    @Test
    public void should_showPages_onStart() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        // When
        presenter.start();

        // Then
        verify(mView).showPages(presenter.getPages(), false);
    }

    @Test
    public void should_scrollToFirstPage_onStart() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        // When
        presenter.start();

        // Then
        verify(mView).scrollToPage(0);
    }

    @Test
    public void should_activatePageIndicator_forFirstPage_onStart() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        // When
        presenter.start();

        // Then
        verify(mView).activatePageIndicatorForPage(0);
    }

    @Test
    public void should_triggerFinishEvent_whenClickingNext_onTheLastPage_withoutEmptyLastPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        final EventTracker eventTracker = spy(EventTracker.class);

        new GiniVision.Builder().setEventTracker(eventTracker).build();

        // When
        presenter.onScrolledToPage(DefaultPagesPhone.asArrayList().size() - 1);
        presenter.showNextPage();

        // Then
        verify(eventTracker).onOnboardingScreenEvent(new Event<>(OnboardingScreenEvent.FINISH));
    }

    @Test
    public void should_triggerFinishEvent_whenClickingNext_onTheLastPage_withEmptyLastPage() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        final EventTracker eventTracker = spy(EventTracker.class);
        new GiniVision.Builder().setEventTracker(eventTracker).build();

        when(mView.slideOutViews()).thenReturn(CompletableFuture.<Void>completedFuture(null));

        // When
        presenter.addEmptyLastPage();
        presenter.onScrolledToPage(DefaultPagesPhone.asArrayList().size());

        // Then
        verify(eventTracker).onOnboardingScreenEvent(new Event<>(OnboardingScreenEvent.FINISH));
    }

    @Test
    public void should_triggerStartEvent() throws Exception {
        // Given
        final OnboardingScreenPresenter presenter = createPresenter();

        final EventTracker eventTracker = spy(EventTracker.class);
        new GiniVision.Builder().setEventTracker(eventTracker).build();

        // When
        presenter.start();

        // Then
        verify(eventTracker).onOnboardingScreenEvent(new Event<>(OnboardingScreenEvent.START));
    }
}