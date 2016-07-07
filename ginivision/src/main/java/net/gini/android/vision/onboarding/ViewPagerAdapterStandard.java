package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapterStandard extends FragmentPagerAdapter {

    private final List<OnboardingPage> mPages;

    public ViewPagerAdapterStandard(@NonNull FragmentManager fm, @NonNull List<OnboardingPage> pages) {
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
    public Fragment getItem(int position) {
        return OnboardingPageFragmentStandard.createInstance(getPages().get(position));
    }
}
