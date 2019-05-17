package net.gini.android.vision.test;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class BackgroundColorMatcher extends TypeSafeMatcher<View> {

    private final int mColor;

    public static BackgroundColorMatcher hasBackgroundColor(final int color) {
        return new BackgroundColorMatcher(color);
    }

    private BackgroundColorMatcher(final int color) {
        mColor = color;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("has background color: " + mColor);
    }

    @Override
    protected boolean matchesSafely(final View item) {
        final Drawable background = item.getBackground();
        if (background instanceof ColorDrawable) {
            final int color = ((ColorDrawable) background).getColor();
            return color == mColor;
        }
        return false;
    }

}
