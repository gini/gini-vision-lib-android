package net.gini.android.vision.internal.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.network.AnalysisResult;
import net.gini.android.vision.util.CancellationToken;
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
    public CancellationToken upload(@NonNull final Document document,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        callback.success(new Result(DEFAULT_DOCUMENT_ID));
        return new CallbackCancellationToken(callback);
    }

    @Override
    public CancellationToken delete(@NonNull final String giniApiDocumentId,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        callback.success(new Result(DEFAULT_DOCUMENT_ID));
        return new CallbackCancellationToken(callback);
    }

    @Override
    public CancellationToken analyze(@NonNull final LinkedHashMap<String, Integer> giniApiDocumentIdRotationMap,
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
        callback.success(createAnalysisResult());
        return new CallbackCancellationToken(callback);
    }

    @NonNull
    protected AnalysisResult createAnalysisResult() {
        return new AnalysisResult(DEFAULT_DOCUMENT_ID,
                Collections.singletonMap("amountToPay",
                        new GiniVisionSpecificExtraction("amountToPay",
                                "1:00EUR", "amountToPay",
                                new GiniVisionBox(1, 0,0,0,0),
                                Collections.<GiniVisionExtraction>emptyList())));
    }

    public class CallbackCancellationToken implements CancellationToken {

        private final GiniVisionNetworkCallback mCallback;

        public CallbackCancellationToken(
                final GiniVisionNetworkCallback callback) {
            mCallback = callback;
        }

        @Override
        public void cancel() {
            mCallback.cancelled();
        }
    }
}
