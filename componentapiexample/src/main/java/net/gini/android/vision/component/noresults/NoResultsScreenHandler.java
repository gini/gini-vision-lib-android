package net.gini.android.vision.component.noresults;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import net.gini.android.vision.component.R;
import net.gini.android.vision.component.camera.CameraExampleAppCompatActivity;
import net.gini.android.vision.noresults.NoResultsFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class NoResultsScreenHandler extends AbstractNoResultsScreenHandler {

    private NoResultsFragmentStandard mNoResultsFragment;

    public NoResultsScreenHandler(final Activity activity) {
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
        actionBar.setTitle("Keine Ergebnisse");
        actionBar.setSubtitle("Wir haben ein paar Tipps für Sie");
    }

    @Override
    protected void setUpActionBar() {

    }
}
