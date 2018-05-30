package net.gini.android.vision.network;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Used by the {@link GiniVisionNetworkService} to return network call results.
 */
public class Result {

    private final String giniApiDocumentId;

    /**
     * Create a new result with a Gini API document id.
     *
     * @param giniApiDocumentId the id of a document in the Gini API
     */
    public Result(final String giniApiDocumentId) {
        this.giniApiDocumentId = giniApiDocumentId;
    }

    /**
     * @return document's id in the Gini API
     */
    public String getGiniApiDocumentId() {
        return giniApiDocumentId;
    }
}
