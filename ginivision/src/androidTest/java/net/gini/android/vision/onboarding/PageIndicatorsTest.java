package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.onboarding.PageIndicatorsHelper.isPageActive;
import static net.gini.android.vision.onboarding.PageIndicatorsHelper.isPageInactive;

import android.widget.LinearLayout;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class PageIndicatorsTest {

    @Test
    public void should_createPageIndicatorImageViews() {
        final OnboardingFragmentImpl.PageIndicators pageIndicators = createPageIndicatorsInstance(2);

        assertThat(pageIndicators.getPageIndicatorImageViews().size()).isEqualTo(2);
    }

    @Test
    public void should_setActiveRequiredPageIndicator() {
        final OnboardingFragmentImpl.PageIndicators pageIndicators = createPageIndicatorsInstance(2);
        pageIndicators.setActive(0);

        isPageActive(pageIndicators, 0);
        isPageInactive(pageIndicators, 1);

        pageIndicators.setActive(1);

        isPageInactive(pageIndicators, 0);
        isPageActive(pageIndicators, 1);
    }

    @NonNull
    private OnboardingFragmentImpl.PageIndicators createPageIndicatorsInstance(final int nrOfPages) {
        final LinearLayout linearLayout = new LinearLayout(ApplicationProvider.getApplicationContext());
        final OnboardingFragmentImpl.PageIndicators pageIndicators =
                new OnboardingFragmentImpl.PageIndicators(
                        ApplicationProvider.getApplicationContext(), nrOfPages, linearLayout);
        pageIndicators.create();
        return pageIndicators;
    }


}
