package net.gini.android.vision.analysis;

import android.icu.math.BigDecimal;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * Created by Alpar Szotyori on 15.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class RotationMatcher extends TypeSafeMatcher<View> {

    private final float mRotation;

    public static RotationMatcher withRotation(final float rotation) {
        return new RotationMatcher(rotation);
    }

    private RotationMatcher(final float rotation) {
        mRotation = rotation;
    }

    @Override
    protected boolean matchesSafely(final View item) {
        return BigDecimal.valueOf(item.getRotation()).equals(BigDecimal.valueOf(mRotation));
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("has rotation " + mRotation);
    }
}
