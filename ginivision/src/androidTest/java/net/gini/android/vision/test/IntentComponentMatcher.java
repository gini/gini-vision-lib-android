package net.gini.android.vision.test;

import android.content.Intent;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class IntentComponentMatcher extends TypeSafeMatcher<Intent> {

    private final String mComponent;

    public IntentComponentMatcher(String component) {
        mComponent = component;
    }

    @Override
    protected boolean matchesSafely(Intent item) {
        return item.getComponent().getClassName().equals(mComponent);
    }

    @Override
    public void describeTo(Description description) {
    }
}
