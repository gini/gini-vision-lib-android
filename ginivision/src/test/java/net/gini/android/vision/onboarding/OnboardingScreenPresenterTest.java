package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Application;
import android.content.res.Resources;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;

import net.gini.android.vision.R;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class OnboardingScreenPresenterTest {

    @Mock
    private Application mApp;
    @Mock
    private OnboardingScreenContract.View mView;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
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
        when(mApp.getResources()).thenReturn(resources);

        return new OnboardingScreenPresenter(mApp, mView);
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
        verify(mView).activatePageIndicatorForPage(0);
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
}