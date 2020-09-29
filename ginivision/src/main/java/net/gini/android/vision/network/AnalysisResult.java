package net.gini.android.vision.network;

import net.gini.android.vision.network.model.GiniVisionCompoundExtraction;
import net.gini.android.vision.network.model.GiniVisionReturnReason;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

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
    private final List<GiniVisionReturnReason> returnReasons;

    /**
     * Create a new analysis result for a Gini API document id.
     *
     * @param giniApiDocumentId the id of a document in the Gini API
     * @param extractions       the extractions from the Gini API
     */
    public AnalysisResult(@NonNull final String giniApiDocumentId,
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        super(giniApiDocumentId);
        this.extractions = extractions;
        this.compoundExtractions = Collections.emptyMap();
        this.returnReasons = Collections.emptyList();
    }

    /**
     * Create a new analysis result for a Gini API document id.
     *
     * @param giniApiDocumentId the id of a document in the Gini API
     * @param extractions       the extractions from the Gini API
     * @param compoundExtractions the compound extractions from the Gini API
     */
    public AnalysisResult(@NonNull final String giniApiDocumentId,
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final Map<String, GiniVisionCompoundExtraction> compoundExtractions) {
        super(giniApiDocumentId);
        this.extractions = extractions;
        this.compoundExtractions = compoundExtractions;
        this.returnReasons = Collections.emptyList();
    }

    /**
     * Create a new analysis result for a Gini API document id.
     *
     * @param giniApiDocumentId the id of a document in the Gini API
     * @param extractions       the extractions from the Gini API
     * @param compoundExtractions the compound extractions from the Gini API
     */
    public AnalysisResult(@NonNull final String giniApiDocumentId,
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final Map<String, GiniVisionCompoundExtraction> compoundExtractions,
            @NonNull final List<GiniVisionReturnReason> returnReasons) {
        super(giniApiDocumentId);
        this.extractions = extractions;
        this.compoundExtractions = compoundExtractions;
        this.returnReasons = returnReasons;
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

    /**
     * @return list of return reasons
     */
    public List<GiniVisionReturnReason> getReturnReasons() {
        return returnReasons;
    }
}
