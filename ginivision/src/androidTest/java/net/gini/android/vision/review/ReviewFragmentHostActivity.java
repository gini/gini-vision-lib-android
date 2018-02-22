package net.gini.android.vision.review;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class ReviewFragmentHostActivity extends
        ReviewFragmentHostActivityNotListener implements ReviewFragmentListener {

    private boolean isRotated;

    public boolean isRotated() {
        return isRotated;
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
        isRotated = true;
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {

    }
}
