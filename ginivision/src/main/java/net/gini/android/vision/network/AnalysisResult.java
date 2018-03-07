package net.gini.android.vision.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class AnalysisResult extends Result {

    private final Map<String, GiniVisionSpecificExtraction> extractions;

    public AnalysisResult(@NonNull final String documentId,
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        super(documentId);
        this.extractions = extractions;
    }

    @NonNull
    public Map<String, GiniVisionSpecificExtraction> getExtractions() {
        return extractions;
    }
}
