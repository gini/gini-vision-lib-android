package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.legacy.app.FragmentPagerAdapter;

class ViewPagerAdapterStandard extends FragmentPagerAdapter {

    private final List<OnboardingPage> mPages;

    public ViewPagerAdapterStandard(@NonNull final FragmentManager fm,
            @NonNull final List<OnboardingPage> pages) {
        super(fm);
        mPages = pages;
    }

    @NonNull
    protected List<OnboardingPage> getPages() {
        return mPages;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public Fragment getItem(final int position) {
        return OnboardingPageFragmentStandard.createInstance(getPages().get(position));
    }
}
