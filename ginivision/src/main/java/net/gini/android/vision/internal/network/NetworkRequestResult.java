package net.gini.android.vision.internal.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.document.GiniVisionDocument;

/**
 * Created by Alpar Szotyori on 16.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class NetworkRequestResult<T extends GiniVisionDocument> {

    private final T mGiniVisionDocument;
    private final String mApiDocumentId;

    public NetworkRequestResult(@NonNull final T giniVisionDocument,
            @NonNull final String apiDocumentId) {
        mGiniVisionDocument = giniVisionDocument;
        mApiDocumentId = apiDocumentId;
    }

    @NonNull
    public T getGiniVisionDocument() {
        return mGiniVisionDocument;
    }

    @NonNull
    public String getApiDocumentId() {
        return mApiDocumentId;
    }
}
