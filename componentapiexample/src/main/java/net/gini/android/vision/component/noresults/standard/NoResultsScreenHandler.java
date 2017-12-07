package net.gini.android.vision.component.noresults.standard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import net.gini.android.vision.component.R;
import net.gini.android.vision.component.camera.compat.CameraExampleAppCompatActivity;
import net.gini.android.vision.component.noresults.BaseNoResultsScreenHandler;
import net.gini.android.vision.noresults.NoResultsFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Creates standard fragments and activities for the No Results Screen.
 */
public class NoResultsScreenHandler extends BaseNoResultsScreenHandler {

    private NoResultsFragmentStandard mNoResultsFragment;

    NoResultsScreenHandler(final Activity activity) {
        super(activity);
    }

    @Override
    protected Intent getCameraActivityIntent() {
        return new Intent(getActivity(), CameraExampleAppCompatActivity.class);
    }

    @Override
    protected void retainNoResultsFragment() {
        mNoResultsFragment =
                (NoResultsFragmentStandard) getActivity().getFragmentManager().findFragmentById(
                        R.id.no_results_screen_container);
    }

    @Override
    protected void showNoResultsFragment() {
        mNoResultsFragment = NoResultsFragmentStandard.createInstance(getDocument());
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.no_results_screen_container, mNoResultsFragment)
        .commit();
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle(R.string.no_results_screen_title);
        actionBar.setSubtitle(getActivity().getString(R.string.no_results_screen_subtitle));
    }

    @Override
    protected void setUpActionBar() {

    }
}
