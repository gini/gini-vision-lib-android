package net.gini.android.vision.onboarding;

import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.R;

import java.util.ArrayList;

/**
 * @exclude
 */
public enum DefaultPagesPhone {
    FLAT(new OnboardingPage(R.string.gv_onboarding_flat, R.drawable.gv_onboarding_flat)),
    PARALLEL(new OnboardingPage(R.string.gv_onboarding_parallel, R.drawable.gv_onboarding_parallel)),
    ALIGN(new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align));

    private final OnboardingPage mOnboardingPage;

    DefaultPagesPhone(OnboardingPage onboardingPage) {
        mOnboardingPage = onboardingPage;
    }

    @VisibleForTesting
    OnboardingPage getPage() {
        return mOnboardingPage;
    }

    public static ArrayList<OnboardingPage> asArrayList() {
        ArrayList<OnboardingPage> arrayList = new ArrayList<>(values().length);
        for (DefaultPagesPhone pages : values()) {
            arrayList.add(pages.getPage());
        }
        return arrayList;
    }
}
