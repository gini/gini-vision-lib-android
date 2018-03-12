package net.gini.android.vision.component.review.standard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

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

/**
 * Creates standard fragments and activities for the Review Screen.
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
    protected ReviewFragmentInterface createReviewFragment() {
        mReviewFragment = ReviewFragmentStandard.createInstance(getDocument());
        return mReviewFragment;
    }

    @Override
    protected void showReviewFragment() {
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.review_screen_container, mReviewFragment)
                .commit();
    }

    @Override
    protected ReviewFragmentInterface retrieveReviewFragment() {
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
        actionBar.setTitle(R.string.review_screen_title);
        actionBar.setSubtitle(getActivity().getString(R.string.review_screen_subtitle));
    }

    @Override
    protected void setUpActionBar() {

    }

    @Override
    public void onAddMorePages(@NonNull final Document document) {

    }
}
