package net.gini.android.vision.onboarding;

import net.gini.android.vision.internal.ui.FragmentImplCallback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

/**
 * @exclude
 */
public interface OnboardingFragmentImplCallback extends FragmentImplCallback {

    PagerAdapter getViewPagerAdapter(@NonNull List<OnboardingPage> pages);
}
