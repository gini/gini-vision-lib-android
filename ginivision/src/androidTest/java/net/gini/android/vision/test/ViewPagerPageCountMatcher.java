package net.gini.android.vision.test;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ViewPagerPageCountMatcher extends TypeSafeMatcher<View> {

    private final int mCount;

    public ViewPagerPageCountMatcher(int count) {
        mCount = count;
    }

    @Override
    protected boolean matchesSafely(View item) {
        if (!(item instanceof ViewPager)) {
            return false;
        }
        ViewPager viewPager = (ViewPager) item;

        PagerAdapter pagerAdapter = viewPager.getAdapter();

        return pagerAdapter != null && pagerAdapter.getCount() == mCount;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("with count: ");
        description.appendValue(mCount);
    }
}
