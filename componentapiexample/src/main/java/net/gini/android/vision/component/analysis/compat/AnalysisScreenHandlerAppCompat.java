package net.gini.android.vision.component.analysis.compat;

import android.app.Activity;
import android.content.Intent;

import net.gini.android.vision.Document;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentInterface;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.BaseAnalysisScreenHandler;
import net.gini.android.vision.component.noresults.compat.NoResultsExampleAppCompatActivity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Creates compatibility library fragments and activities for the Analysis Screen.
 */
public class AnalysisScreenHandlerAppCompat extends BaseAnalysisScreenHandler {

    private final AppCompatActivity mAppCompatActivity;
    private AnalysisFragmentCompat mAnalysisFragment;

    AnalysisScreenHandlerAppCompat(final Activity activity) {
        super(activity);
        mAppCompatActivity = (AppCompatActivity) activity;
    }

    @Override
    protected Intent getNoResultsActivityIntent(final Document document) {
        return NoResultsExampleAppCompatActivity.newInstance(document, mAppCompatActivity);
    }

    @Override
    protected AnalysisFragmentInterface retrieveAnalysisFragment() {
        mAnalysisFragment =
                (AnalysisFragmentCompat) mAppCompatActivity.getSupportFragmentManager()
                        .findFragmentById(R.id.analysis_screen_container);
        return mAnalysisFragment;
    }

    @Override
    protected void showAnalysisFragment() {
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.analysis_screen_container, mAnalysisFragment)
                .commit();
    }

    @Override
    protected AnalysisFragmentInterface createAnalysisFragment() {
        mAnalysisFragment = AnalysisFragmentCompat.createInstance(getDocument(),
                getErrorMessageFromReviewScreen());
        return mAnalysisFragment;
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("");
        actionBar.setSubtitle(mAppCompatActivity.getString(R.string.one_moment_please));
    }

    @Override
    protected void setUpActionBar() {
        mAppCompatActivity.setSupportActionBar(
                (Toolbar) mAppCompatActivity.findViewById(R.id.toolbar));
    }


}
