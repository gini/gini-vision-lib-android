package net.gini.android.vision.component.review;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.component.R;
import net.gini.android.vision.review.ReviewFragmentCompat;

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
    protected void callOnDocumentAnalyzed() {
        mReviewFragment.onDocumentAnalyzed();
    }

    @Override
    protected void showReviewFragment() {
        mReviewFragment = ReviewFragmentCompat.createInstance(getDocument());
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.review_screen_container, mReviewFragment)
                .commit();
    }

    @Override
    protected void retainReviewFragment() {
        mReviewFragment =
                (ReviewFragmentCompat) mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                        R.id.review_screen_container);
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
