package net.gini.android.vision.onboarding;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.util.FeatureConfiguration;

import java.util.ArrayList;

import androidx.annotation.VisibleForTesting;

/**
 * Internal use only.
 *
 * @suppress
 */
public enum DefaultPagesTablet {
    LIGHTING(
            new OnboardingPage(R.string.gv_onboarding_lighting, R.drawable.gv_onboarding_lighting)),
    FLAT(new OnboardingPage(R.string.gv_onboarding_flat, R.drawable.gv_onboarding_flat)),
    PARALLEL(
            new OnboardingPage(R.string.gv_onboarding_parallel, R.drawable.gv_onboarding_parallel)),
    ALIGN(new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align,
            false, true));

    private final OnboardingPage mOnboardingPage;

    DefaultPagesTablet(final OnboardingPage onboardingPage) {
        mOnboardingPage = onboardingPage;
    }

    @VisibleForTesting
    OnboardingPage getPage() {
        return mOnboardingPage;
    }

    public static ArrayList<OnboardingPage> asArrayList() { // NOPMD - ArrayList required (Bundle)
        final ArrayList<OnboardingPage> arrayList = new ArrayList<>(values().length);
        for (final DefaultPagesTablet pages : values()) {
            arrayList.add(pages.getPage());
        }
        if (FeatureConfiguration.isMultiPageEnabled()) {
            arrayList.add(ConditionalPages.MULTI_PAGE.getPage());
        }
        return arrayList;
    }
}
