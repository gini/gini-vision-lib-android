package net.gini.android.vision.internal.network;

import net.gini.android.vision.document.GiniVisionDocument;

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
