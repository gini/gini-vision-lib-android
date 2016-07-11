package net.gini.android.vision.onboarding;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;

import net.gini.android.vision.ui.FragmentImplCallback;

import java.util.List;

public interface OnboardingFragmentImplCallback extends FragmentImplCallback {

    PagerAdapter getViewPagerAdapter(@NonNull List<OnboardingPage> pages);
}
