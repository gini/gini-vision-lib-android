package net.gini.android.vision.component.review.compat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.Document;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.compat.AnalysisExampleAppCompatActivity;
import net.gini.android.vision.component.noresults.compat.NoResultsExampleAppCompatActivity;
import net.gini.android.vision.component.review.BaseReviewScreenHandler;
import net.gini.android.vision.review.ReviewFragmentCompat;
import net.gini.android.vision.review.ReviewFragmentInterface;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Creates compatibility library fragments and activities for the Review Screen.
 */
public class ReviewScreenHandlerAppCompat extends BaseReviewScreenHandler {

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
    protected ReviewFragmentInterface createReviewFragment() {
        mReviewFragment = ReviewFragmentCompat.createInstance(getDocument());
        return mReviewFragment;
    }

    @Override
    protected void showReviewFragment() {
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.review_screen_container, mReviewFragment)
                .commit();
    }

    @Override
    protected ReviewFragmentInterface retrieveReviewFragment() {
        mReviewFragment =
                (ReviewFragmentCompat) mAppCompatActivity.getSupportFragmentManager()
                        .findFragmentById(R.id.review_screen_container);
        return mReviewFragment;
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(R.string.review_screen_title);
        actionBar.setSubtitle(mAppCompatActivity.getString(R.string.review_screen_subtitle));
    }

    @Override
    protected void setUpActionBar() {
        mAppCompatActivity.setSupportActionBar(
                (Toolbar) mAppCompatActivity.findViewById(R.id.toolbar));
    }
}
