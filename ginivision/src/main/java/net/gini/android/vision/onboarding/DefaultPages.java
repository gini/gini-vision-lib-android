package net.gini.android.vision.onboarding;

import net.gini.android.vision.R;

import java.util.ArrayList;

/**
 * @exclude
 */
public final class DefaultPages {

    private static final ArrayList<OnboardingPage> DEFAULT_PAGES = new ArrayList<>(3);

    static {
        DEFAULT_PAGES.add(new OnboardingPage(R.string.gv_onboarding_flat, R.drawable.gv_onboarding_flat));
        DEFAULT_PAGES.add(new OnboardingPage(R.string.gv_onboarding_parallel, R.drawable.gv_onboarding_parallel));
        DEFAULT_PAGES.add(new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align));
    }

    public static ArrayList<OnboardingPage> getPages() {
        return DEFAULT_PAGES;
    }

    private DefaultPages() {

    }
}
