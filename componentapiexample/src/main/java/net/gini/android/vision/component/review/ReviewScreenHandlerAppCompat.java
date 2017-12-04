package net.gini.android.vision.component.review;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.Document;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.AnalysisExampleAppCompatActivity;
import net.gini.android.vision.component.noresults.NoResultsExampleAppCompatActivity;
import net.gini.android.vision.review.ReviewFragmentCompat;
import net.gini.android.vision.review.ReviewFragmentInterface;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class ReviewScreenHandlerAppCompat extends AbstractReviewScreenHandler {

    private final AppCompatActivity mAppCompatActivity;
    private ReviewFragmentCompat mReviewFragment;

    ReviewScreenHandlerAppCompat(final Activity activity) {
        super(activity);
        mAppCompatActivity = (AppCompatActivity) activity;
    }

    @Override
    protected Intent getAnalysisActivityIntent(final Document document, final String errorMessage) {
        return AnalysisExampleAppCompatActivity.newInstance(document, errorMessage,
                mAppCompatActivity);
    }

    @Override
    protected Intent getNoResultsActivityIntent(final Document document) {
        return NoResultsExampleAppCompatActivity.newInstance(document, mAppCompatActivity);
    }

    @Override
    protected ReviewFragmentInterface showReviewFragment() {
        mReviewFragment = ReviewFragmentCompat.createInstance(getDocument());
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.review_screen_container, mReviewFragment)
                .commit();
        return mReviewFragment;
    }

    @Override
    protected ReviewFragmentInterface retainReviewFragment() {
        mReviewFragment =
                (ReviewFragmentCompat) mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                        R.id.review_screen_container);
        return mReviewFragment;
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("Seite überprüfen");
        actionBar.setSubtitle("Vollständig, scharf und in Leserichtung?");
    }

    @Override
    protected void setUpActionBar() {
        mAppCompatActivity.setSupportActionBar(
                (Toolbar) mAppCompatActivity.findViewById(R.id.toolbar));
    }
}
