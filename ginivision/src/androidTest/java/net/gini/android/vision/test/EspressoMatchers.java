package net.gini.android.vision.test;

import android.content.Intent;
import android.view.View;

import org.hamcrest.Matcher;

public class EspressoMatchers {

    public static Matcher<View> hasPageCount(int count) {
        return new ViewPagerPageCountMatcher(count);
    }

    public static Matcher<Intent> hasComponent(String component) {
        return new IntentComponentMatcher(component);
    }
}
