package net.gini.android.vision.test;

import static org.hamcrest.MatcherAssert.assertThat;

import android.view.View;

import org.hamcrest.number.IsCloseTo;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;

public class ViewSizeRatioAssertion implements ViewAssertion {

    private final double sizeRatio;

    public ViewSizeRatioAssertion(final double sizeRatio) {
        this.sizeRatio = sizeRatio;
    }

    @Override
    public void check(final View view, final NoMatchingViewException noViewFoundException) {
        assertThat("View size ratio (w/h) is not " + sizeRatio, sizeRatio,
                IsCloseTo.closeTo((double) view.getWidth() / view.getHeight(), 0.001));
    }
}
