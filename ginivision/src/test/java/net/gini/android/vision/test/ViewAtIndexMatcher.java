package net.gini.android.vision.test;

import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class ViewAtIndexMatcher extends TypeSafeMatcher<View> {

    private final Matcher<View> mMatcher;
    private final int mIndex;
    private int mCurrentIndex = 0;

    public static ViewAtIndexMatcher withIndex(final int index,
            @NonNull final Matcher<View> matcher) {
        return new ViewAtIndexMatcher(index, matcher);
    }

    public ViewAtIndexMatcher(final int index, @NonNull final Matcher<View> matcher) {
        mIndex = index;
        mMatcher = matcher;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("with index: ");
        description.appendValue(mIndex);
        description.appendText(" that ");
        mMatcher.describeTo(description);
    }

    @Override
    public boolean matchesSafely(final View view) {
        if (mMatcher.matches(view)) {
            final int viewIndex = mCurrentIndex;
            mCurrentIndex++;
            return viewIndex == mIndex;
        }
        return false;
    }

}
