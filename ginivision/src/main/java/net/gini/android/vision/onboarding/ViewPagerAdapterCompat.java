package net.gini.android.vision.onboarding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class ViewPagerAdapterCompat extends FragmentPagerAdapter {

    private final List<OnboardingPage> mPages;

    public ViewPagerAdapterCompat(@NonNull final FragmentManager fm,
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
        return OnboardingPageFragmentCompat.createInstance(getPages().get(position));
    }
}
