package net.gini.android.vision.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.network.model.GiniVisionCompoundExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Used by the {@link GiniVisionNetworkService} to return analysis results.
 */
public class AnalysisResult extends Result {

    private final Map<String, GiniVisionSpecificExtraction> extractions;
    private final Map<String, GiniVisionCompoundExtraction> compoundExtractions;

    /**
     * Create a new analysis result for a Gini API document id.
     *
     * @param giniApiDocumentId the id of a document in the Gini API
     * @param extractions       the extractions from the Gini API
     */
    public AnalysisResult(@NonNull final String giniApiDocumentId,
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final Map<String, GiniVisionCompoundExtraction> compoundExtractions) {
        super(giniApiDocumentId);
        this.extractions = extractions;
        this.compoundExtractions = compoundExtractions;
    }

    /**
     * @return map of extraction labels and specific extractions
     */
    @NonNull
    public Map<String, GiniVisionSpecificExtraction> getExtractions() {
        return extractions;
    }

    /**
     * @return map of extraction labels and compound extractions
     */
    @NonNull
    public Map<String, GiniVisionCompoundExtraction> getCompoundExtractions() {
        return compoundExtractions;
    }
}
