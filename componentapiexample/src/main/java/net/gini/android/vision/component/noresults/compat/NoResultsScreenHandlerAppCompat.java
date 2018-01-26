package net.gini.android.vision.component.noresults.compat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.component.R;
import net.gini.android.vision.component.camera.compat.CameraExampleAppCompatActivity;
import net.gini.android.vision.component.noresults.BaseNoResultsScreenHandler;
import net.gini.android.vision.noresults.NoResultsFragmentCompat;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Creates compatibility library fragments and activities for the No Results Screen.
 */
public class NoResultsScreenHandlerAppCompat extends BaseNoResultsScreenHandler {

    private final AppCompatActivity mAppCompatActivity;
    private NoResultsFragmentCompat mNoResultsFragment;

    NoResultsScreenHandlerAppCompat(final Activity activity) {
        super(activity);
        mAppCompatActivity = (AppCompatActivity) activity;
    }

    @Override
    protected Intent getCameraActivityIntent() {
        return new Intent(mAppCompatActivity, CameraExampleAppCompatActivity.class);
    }

    @Override
    protected void retainNoResultsFragment() {
        mNoResultsFragment =
                (NoResultsFragmentCompat) mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                        R.id.no_results_screen_container);
    }

    @Override
    protected void showNoResultsFragment() {
        mNoResultsFragment = NoResultsFragmentCompat.createInstance(getDocument());
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.no_results_screen_container, mNoResultsFragment)
                .commit();
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(R.string.no_results_screen_title);
        actionBar.setSubtitle(mAppCompatActivity.getString(R.string.no_results_screen_subtitle));
    }

    @Override
    protected void setUpActionBar() {
        mAppCompatActivity.setSupportActionBar(
                (Toolbar) mAppCompatActivity.findViewById(R.id.toolbar));
    }
}
