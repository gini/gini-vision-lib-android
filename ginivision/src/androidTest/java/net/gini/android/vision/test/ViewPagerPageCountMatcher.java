package net.gini.android.vision.test;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerPageCountMatcher extends TypeSafeMatcher<View> {

    private final int mCount;

    public ViewPagerPageCountMatcher(final int count) {
        mCount = count;
    }

    @Override
    protected boolean matchesSafely(final View item) {
        if (!(item instanceof ViewPager)) {
            return false;
        }
        final ViewPager viewPager = (ViewPager) item;

        final PagerAdapter pagerAdapter = viewPager.getAdapter();

        return pagerAdapter != null && pagerAdapter.getCount() == mCount;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("with count: ");
        description.appendValue(mCount);
    }
}
