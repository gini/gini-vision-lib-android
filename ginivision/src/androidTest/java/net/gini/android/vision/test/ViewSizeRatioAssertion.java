package net.gini.android.vision.test;

import static org.hamcrest.MatcherAssert.assertThat;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.view.View;

import org.hamcrest.number.IsCloseTo;

public class ViewSizeRatioAssertion implements ViewAssertion {

    private final double sizeRatio;

    public ViewSizeRatioAssertion(final double sizeRatio) {
        this.sizeRatio = sizeRatio;
    }

    @Override
    public void check(final View view, final NoMatchingViewException noViewFoundException) {
        assertThat("View size ratio (w/h) is not " + sizeRatio, sizeRatio, IsCloseTo.closeTo((double) view.getWidth() / view.getHeight(), 0.001));
    }
}
