package net.gini.android.vision.onboarding;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapterStandard extends FragmentPagerAdapter {

    private final List<OnboardingPage> mPages;

    public ViewPagerAdapterStandard(FragmentManager fm, List<OnboardingPage> pages) {
        super(fm);
        mPages = pages;
    }

    protected List<OnboardingPage> getPages() {
        return mPages;
    }

    @Override
    public int getCount() {
        return mPages.size();
    }

    @Override
    public Fragment getItem(int position) {
        boolean noBackground = isLastItem(position);
        return OnboardingPageFragmentStandard.createInstance(getPages().get(position), noBackground);
    }

    private boolean isLastItem(int position) {
        return position == getCount() - 1;
    }
}
