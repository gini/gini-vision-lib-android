package net.gini.android.vision.review;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class ReviewFragmentHostActivity extends
        ReviewFragmentHostActivityNotListener implements ReviewFragmentListener {

    private boolean shouldAnalyzeDocument;

    public boolean shouldAnalyzeDocument() {
        return shouldAnalyzeDocument;
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {

    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document) {

    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {

    }

    @Override
    public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation,
            final int newRotation) {
        shouldAnalyzeDocument = true;
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {

    }

    @Override
    public void onExtractionsAvailable(
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {

    }

    @Override
    public void onProceedToNoExtractionsScreen(@NonNull final Document document) {

    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document,
            @Nullable final String errorMessage) {

    }
}
