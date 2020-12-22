package net.gini.android.vision.component.analysis.standard;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.analysis.AnalysisFragmentInterface;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.BaseAnalysisScreenHandler;
import net.gini.android.vision.component.noresults.standard.NoResultsExampleActivity;
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction;
import net.gini.android.vision.network.model.GiniVisionReturnReason;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.List;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Creates standard fragments and activities for the Analysis Screen.
 */
public class AnalysisScreenHandler extends BaseAnalysisScreenHandler {

    private AnalysisFragmentStandard mAnalysisFragment;

    AnalysisScreenHandler(final Activity activity) {
        super(activity);
    }

    @Override
    protected Intent getNoResultsActivityIntent(final Document document) {
        return NoResultsExampleActivity.newInstance(document, getActivity());
    }

    @Override
    protected AnalysisFragmentInterface retrieveAnalysisFragment() {
        mAnalysisFragment =
                (AnalysisFragmentStandard) getActivity().getFragmentManager().findFragmentById(
                        R.id.analysis_screen_container);
        return mAnalysisFragment;
    }

    @Override
    protected void showAnalysisFragment() {
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.analysis_screen_container, mAnalysisFragment)
                .commit();
    }

    @Override
    protected AnalysisFragmentInterface createAnalysisFragment() {
        mAnalysisFragment = AnalysisFragmentStandard.createInstance(getDocument(),
                getErrorMessageFromReviewScreen());
        return mAnalysisFragment;
    }

    @Override
    protected void setTitles() {
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("");
        actionBar.setSubtitle(getActivity().getString(R.string.one_moment_please));
    }

    @Override
    protected void setUpActionBar() {
    }

}
