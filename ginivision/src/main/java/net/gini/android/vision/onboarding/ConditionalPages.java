package net.gini.android.vision.onboarding;

import net.gini.android.vision.R;

import java.util.ArrayList;

/**
 * Internal use only.
 *
 * @suppress
 */
public enum ConditionalPages {
    MULTI_PAGE(new OnboardingPage(R.string.gv_onboarding_multi_page, R.drawable.gv_onboarding_multipage));

    private final OnboardingPage mOnboardingPage;

    ConditionalPages(final OnboardingPage onboardingPage) {
        mOnboardingPage = onboardingPage;
    }

    OnboardingPage getPage() {
        return mOnboardingPage;
    }

    public static ArrayList<OnboardingPage> asArrayList() { // NOPMD - ArrayList required (Bundle)
        final ArrayList<OnboardingPage> arrayList = new ArrayList<>(values().length);
        for (final ConditionalPages pages : values()) {
            arrayList.add(pages.getPage());
        }
        return arrayList;
    }
}
