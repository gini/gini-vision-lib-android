package net.gini.android.vision.internal.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.network.AnalysisResult;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetworkCallback;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.Result;
import net.gini.android.vision.network.model.GiniVisionBox;
import net.gini.android.vision.network.model.GiniVisionExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * Created by Alpar Szotyori on 16.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class GiniVisionNetworkServiceStub implements GiniVisionNetworkService {

    public static final String DEFAULT_DOCUMENT_ID = "ABCD-EFGH";

    @Override
    public void upload(@NonNull final Document document,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        callback.success(new Result(DEFAULT_DOCUMENT_ID));
    }

    @Override
    public void delete(@NonNull final String documentId,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        callback.success(new Result(DEFAULT_DOCUMENT_ID));
    }

    @Override
    public void analyze(@NonNull final LinkedHashMap<String, Integer> documentIdRotationMap,
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
        final AnalysisResult analysisResult = new AnalysisResult(DEFAULT_DOCUMENT_ID,
                Collections.singletonMap("amountToPay",
                        new GiniVisionSpecificExtraction("amountToPay",
                                "1:00EUR", "amountToPay",
                                new GiniVisionBox(1, 0,0,0,0),
                                Collections.<GiniVisionExtraction>emptyList())));
        callback.success(analysisResult);
    }

    @Override
    public void cancel(@NonNull final Document document) {

    }

    @Override
    public void cancelAll() {

    }
}
