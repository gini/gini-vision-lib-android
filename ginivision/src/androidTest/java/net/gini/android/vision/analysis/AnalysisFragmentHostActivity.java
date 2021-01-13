package net.gini.android.vision.analysis;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

import androidx.annotation.NonNull;

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

    @Override
    public void onExtractionsAvailable(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {

    }

    @Override
    public void onProceedToNoExtractionsScreen(@NonNull final Document document) {

    }

    @Override
    public void onDefaultPDFAppAlertDialogCancelled() {

    }
}
