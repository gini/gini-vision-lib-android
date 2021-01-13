package net.gini.android.vision.onboarding;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import android.app.Activity;
import android.content.res.Resources;

import net.gini.android.vision.R;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class OnboardingPagePresenterTest {

    @Mock
    private Activity mActivity;
    @Mock
    private OnboardingPageContract.View mView;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void should_showImage_onStart() throws Exception {
        // Given
        final OnboardingPage page = DefaultPagesPhone.ALIGN.getPage();

        final OnboardingPagePresenter presenter = createPresenter(page);

        // When
        presenter.start();

        // Then
        verify(mView).showImage(page.getImageResId(), false);
    }

    @NonNull
    private OnboardingPagePresenter createPresenter(@NonNull final OnboardingPage page) throws Exception {
        return createPresenter(page, true);
    }

    @NonNull
    private OnboardingPagePresenter createPresenter(@NonNull final OnboardingPage page,
            final Boolean isPortrait) throws Exception {
        final Resources resources = mock(Resources.class);
        when(resources.getBoolean(R.bool.gv_is_portrait)).thenReturn(isPortrait);
        when(mActivity.getResources()).thenReturn(resources);

        final OnboardingPagePresenter presenter = new OnboardingPagePresenter(mActivity, mView);
        presenter.setPage(page);

        return presenter;
    }

    @Test
    public void should_notShowImage_whenNotAvailable() throws Exception {
        // Given
        final OnboardingPage page = new OnboardingPage(R.string.gv_onboarding_align, 0);

        final OnboardingPagePresenter presenter = createPresenter(page);

        // When
        presenter.start();

        // Then
        verify(mView, never()).showImage(page.getImageResId(), false);
    }

    @Test
    public void should_showRotatedImage_inLandscape_whenRequested() throws Exception {
        // Given
        final OnboardingPage page = new OnboardingPage(R.string.gv_onboarding_align,
                R.drawable.gv_onboarding_align, true, true);

        final OnboardingPagePresenter presenter = createPresenter(page, false);

        // When
        presenter.start();

        // Then
        verify(mView).showImage(page.getImageResId(), true);
    }

    @Test
    public void should_showText_onStart() throws Exception {
        // Given
        final OnboardingPage page = DefaultPagesPhone.ALIGN.getPage();

        final OnboardingPagePresenter presenter = createPresenter(page);

        // When
        presenter.start();

        // Then
        verify(mView).showText(page.getTextResId());
    }

    @Test
    public void should_notShowText_whenNotAvailable() throws Exception {
        // Given
        final OnboardingPage page = new OnboardingPage(0, R.drawable.gv_onboarding_align);

        final OnboardingPagePresenter presenter = createPresenter(page);

        // When
        presenter.start();

        // Then
        verify(mView, never()).showText(page.getTextResId());
    }

    @Test
    public void should_showTransparentBackground_whenRequested() throws Exception {
        // Given
        final OnboardingPage page = new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align,true);

        final OnboardingPagePresenter presenter = createPresenter(page);

        // When
        presenter.start();

        // Then
        verify(mView).showTransparentBackground();
    }

    @Test
    public void should_notShowTransparentBackground_whenNotRequested() throws Exception {
        // Given
        final OnboardingPage page = new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align,false);

        final OnboardingPagePresenter presenter = createPresenter(page);

        // When
        presenter.start();

        // Then
        verify(mView, never()).showTransparentBackground();
    }
}