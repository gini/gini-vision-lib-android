package net.gini.android.vision.component.review;

import android.app.ActionBar;
import android.app.Activity;

import net.gini.android.vision.component.R;
import net.gini.android.vision.review.ReviewFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class ReviewScreenHandler extends AbstractReviewScreenHandler {

    private ReviewFragmentStandard mReviewFragment;

    ReviewScreenHandler(final Activity activity) {
        super(activity);
    }

    @Override
    protected void callOnDocumentAnalyzed() {
        mReviewFragment.onDocumentAnalyzed();
    }

    @Override
    protected void showReviewFragment() {
        mReviewFragment = ReviewFragmentStandard.createInstance(getDocument());
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.review_screen_container, mReviewFragment)
                .commit();
    }

    @Override
    protected void retainReviewFragment() {
        mReviewFragment =
                (ReviewFragmentStandard) getActivity().getFragmentManager().findFragmentById(
                        R.id.review_screen_container);
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("Seite überprüfen");
        actionBar.setSubtitle("Vollständig, scharf und in Leserichtung?");
    }

    @Override
    protected void setUpActionBar() {

    }
}
