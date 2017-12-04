package net.gini.android.vision.component.analysis;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentInterface;
import net.gini.android.vision.component.R;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class AnalysisScreenHandlerAppCompat extends AbstractAnalysisScreenHandler {

    private final AppCompatActivity mAppCompatActivity;
    private AnalysisFragmentCompat mAnalysisFragment;

    AnalysisScreenHandlerAppCompat(final Activity activity) {
        super(activity);
        mAppCompatActivity = (AppCompatActivity) activity;
    }

    @Override
    protected AnalysisFragmentInterface retainAnalysisFragment() {
        mAnalysisFragment =
                (AnalysisFragmentCompat) mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                        R.id.analysis_screen_container);
        return mAnalysisFragment;
    }

    @Override
    protected AnalysisFragmentInterface showAnalysisFragment() {
        mAnalysisFragment = AnalysisFragmentCompat.createInstance(getDocument(),
                getErrorMessageFromReviewScreen());
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.analysis_screen_container, mAnalysisFragment)
                .commit();
        return mAnalysisFragment;
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("");
        actionBar.setSubtitle("Einen Moment bitte ...");
    }

    @Override
    protected void setUpActionBar() {
        mAppCompatActivity.setSupportActionBar(
                (Toolbar) mAppCompatActivity.findViewById(R.id.toolbar));
    }


}
