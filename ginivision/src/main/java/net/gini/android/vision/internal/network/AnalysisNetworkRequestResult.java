package net.gini.android.vision.internal.network;

import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.network.AnalysisResult;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 16.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class AnalysisNetworkRequestResult<T extends GiniVisionDocument>
        extends NetworkRequestResult<T> {

    private final AnalysisResult mAnalysisResult;

    public AnalysisNetworkRequestResult(@NonNull final T giniVisionDocument,
            @NonNull final String apiDocumentId,
            @NonNull final AnalysisResult analysisResult) {
        super(giniVisionDocument, apiDocumentId);
        mAnalysisResult = analysisResult;
    }

    @NonNull
    public AnalysisResult getAnalysisResult() {
        return mAnalysisResult;
    }
}
