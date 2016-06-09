package net.gini.android.vision.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.gini.android.vision.R;

import java.util.ArrayList;

class OnboardingFragmentImpl {

    private static final ArrayList<OnboardingPage> DEFAULT_PAGES = new ArrayList<>(3);

    static {
        DEFAULT_PAGES.add(new OnboardingPage(R.string.gv_onboarding_flat, R.drawable.gv_onboarding_flat));
        DEFAULT_PAGES.add(new OnboardingPage(R.string.gv_onboarding_parallel, R.drawable.gv_onboarding_parallel));
        DEFAULT_PAGES.add(new OnboardingPage(R.string.gv_onboarding_align, R.drawable.gv_onboarding_align));
    }

    private final ArrayList<OnboardingPage> mPages;

    public OnboardingFragmentImpl() {
        mPages = DEFAULT_PAGES;
    }

    public OnboardingFragmentImpl(ArrayList<OnboardingPage> pages) {
        mPages = pages != null ? pages : DEFAULT_PAGES;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gv_fragment_onboarding, container, false);
    }

}
