package net.gini.android.vision.analysis;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 14.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class DrawableMatcher extends TypeSafeMatcher<View> {

    private final Drawable mDrawable;

    public static DrawableMatcher withDrawable(@Nullable final Drawable drawable) {
        return new DrawableMatcher(drawable);
    }

    public DrawableMatcher(final Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("has drawable " + mDrawable);
    }

    @Override
    protected boolean matchesSafely(final View item) {
        if (item instanceof ImageView) {
            final ImageView imageView = (ImageView) item;
            return imageView.getDrawable() == mDrawable;
        }
        return false;
    }
}
