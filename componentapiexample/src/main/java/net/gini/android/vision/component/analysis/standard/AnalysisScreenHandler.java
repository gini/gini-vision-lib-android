package net.gini.android.vision.component.analysis.standard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import net.gini.android.vision.Document;
import net.gini.android.vision.analysis.AnalysisFragmentInterface;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.AbstractAnalysisScreenHandler;
import net.gini.android.vision.component.noresults.standard.NoResultsExampleActivity;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class AnalysisScreenHandler extends AbstractAnalysisScreenHandler {

    private AnalysisFragmentStandard mAnalysisFragment;

    AnalysisScreenHandler(final Activity activity) {
        super(activity);
    }

    @Override
    protected Intent getNoResultsActivityIntent(final Document document) {
        return NoResultsExampleActivity.newInstance(document, getActivity());
    }

    @Override
    protected AnalysisFragmentInterface retainAnalysisFragment() {
        mAnalysisFragment =
                (AnalysisFragmentStandard) getActivity().getFragmentManager().findFragmentById(
                        R.id.analysis_screen_container);
        return mAnalysisFragment;
    }

    @Override
    protected AnalysisFragmentInterface showAnalysisFragment() {
        mAnalysisFragment = AnalysisFragmentStandard.createInstance(getDocument(),
                getErrorMessageFromReviewScreen());
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.analysis_screen_container, mAnalysisFragment)
                .commit();
        return mAnalysisFragment;
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("");
        actionBar.setSubtitle("Einen Moment bitte ...");
    }

    @Override
    protected void setUpActionBar() {
    }


}
