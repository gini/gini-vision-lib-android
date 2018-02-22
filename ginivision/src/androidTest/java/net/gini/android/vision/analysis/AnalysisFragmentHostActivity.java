package net.gini.android.vision.analysis;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class AnalysisFragmentHostActivity extends
        AnalysisFragmentHostActivityNotListener implements AnalysisFragmentListener {

    private boolean analysisRequested;

    public boolean isAnalysisRequested() {
        return analysisRequested;
    }

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        analysisRequested = true;
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {

    }
}
