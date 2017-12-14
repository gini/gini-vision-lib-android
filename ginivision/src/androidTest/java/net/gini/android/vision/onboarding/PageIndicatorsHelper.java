package net.gini.android.vision.onboarding;

import static com.google.common.truth.Truth.assertAbout;

import static net.gini.android.vision.onboarding.PageIndicatorImageViewSubject.pageIndicatorImageView;

import net.gini.android.vision.R;

public final class PageIndicatorsHelper {

    public static void isPageActive(OnboardingFragmentImpl.PageIndicators pageIndicators,
            int pageNr) {
        assertAbout(pageIndicatorImageView()).that(
                pageIndicators.getPageIndicators().get(pageNr)).showsDrawable(
                R.drawable.gv_onboarding_indicator_active);
    }

    public static void isPageInactive(OnboardingFragmentImpl.PageIndicators pageIndicators,
            int pageNr) {
        assertAbout(pageIndicatorImageView()).that(
                pageIndicators.getPageIndicators().get(pageNr)).showsDrawable(
                R.drawable.gv_onboarding_indicator_inactive);
    }

    private PageIndicatorsHelper() {
    }
}
