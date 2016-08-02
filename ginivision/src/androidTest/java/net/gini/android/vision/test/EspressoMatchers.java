package net.gini.android.vision.test;

import android.view.View;

import org.hamcrest.Matcher;

public class EspressoMatchers {

    public static Matcher<View> hasPageCount(int count) {
        return new ViewPagerPageCountMatcher(count);
    }
}
