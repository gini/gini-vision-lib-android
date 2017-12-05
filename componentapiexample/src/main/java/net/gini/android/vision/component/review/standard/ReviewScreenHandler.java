package net.gini.android.vision.component.review.standard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import net.gini.android.vision.Document;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.standard.AnalysisExampleActivity;
import net.gini.android.vision.component.noresults.standard.NoResultsExampleActivity;
import net.gini.android.vision.component.review.BaseReviewScreenHandler;
import net.gini.android.vision.review.ReviewFragmentInterface;
import net.gini.android.vision.review.ReviewFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class ReviewScreenHandler extends BaseReviewScreenHandler {

    private ReviewFragmentStandard mReviewFragment;

    ReviewScreenHandler(final Activity activity) {
        super(activity);
    }

    @Override
    protected Intent getAnalysisActivityIntent(final Document document, final String errorMessage) {
        return AnalysisExampleActivity.newInstance(document, errorMessage, getActivity());
    }

    @Override
    protected Intent getNoResultsActivityIntent(final Document document) {
        return NoResultsExampleActivity.newInstance(document, getActivity());
    }

    @Override
    protected ReviewFragmentInterface showReviewFragment() {
        mReviewFragment = ReviewFragmentStandard.createInstance(getDocument());
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.review_screen_container, mReviewFragment)
                .commit();
        return mReviewFragment;
    }

    @Override
    protected ReviewFragmentInterface retainReviewFragment() {
        mReviewFragment =
                (ReviewFragmentStandard) getActivity().getFragmentManager().findFragmentById(
                        R.id.review_screen_container);
        return mReviewFragment;
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
