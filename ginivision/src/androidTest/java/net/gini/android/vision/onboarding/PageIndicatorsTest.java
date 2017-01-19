package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertThat;
import static net.gini.android.vision.onboarding.PageIndicatorsHelper.isPageActive;
import static net.gini.android.vision.onboarding.PageIndicatorsHelper.isPageInactive;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.widget.LinearLayout;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@Ignore
@RunWith(AndroidJUnit4.class)
public class PageIndicatorsTest {

    @Test
    public void should_createPageIndicatorImageViews() {
        OnboardingFragmentImpl.PageIndicators pageIndicators = createPageIndicatorsInstance(2);

        assertThat(pageIndicators.getPageIndicators().size()).isEqualTo(2);
    }

    @Test
    public void should_setActiveRequiredPageIndicator() {
        OnboardingFragmentImpl.PageIndicators pageIndicators = createPageIndicatorsInstance(2);
        pageIndicators.setActive(0);

        isPageActive(pageIndicators, 0);
        isPageInactive(pageIndicators, 1);

        pageIndicators.setActive(1);

        isPageInactive(pageIndicators, 0);
        isPageActive(pageIndicators, 1);
    }

    @NonNull
    private OnboardingFragmentImpl.PageIndicators createPageIndicatorsInstance(int nrOfPages) {
        LinearLayout linearLayout = new LinearLayout(InstrumentationRegistry.getTargetContext());
        OnboardingFragmentImpl.PageIndicators pageIndicators =
                new OnboardingFragmentImpl.PageIndicators(InstrumentationRegistry.getTargetContext(), nrOfPages, linearLayout);
        pageIndicators.create();
        return pageIndicators;
    }


}
