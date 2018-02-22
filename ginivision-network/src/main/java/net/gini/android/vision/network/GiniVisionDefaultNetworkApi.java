package net.gini.android.vision.network;

import android.support.annotation.NonNull;

import net.gini.android.DocumentTaskManager;
import net.gini.android.Gini;
import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.network.model.SpecificExtractionMapper;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionDefaultNetworkApi implements GiniVisionNetworkApi {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionDefaultNetworkApi.class);

    private final SingleDocumentAnalyzer mSingleDocumentAnalyzer;
    private final Gini mGiniApi;
    private final UIExecutor mUIExecutor = new UIExecutor();

    public static Builder builder() {
        return new Builder();
    }

    GiniVisionDefaultNetworkApi(
            @NonNull final Gini giniApi,
            @NonNull final SingleDocumentAnalyzer singleDocumentAnalyzer) {
        mSingleDocumentAnalyzer = singleDocumentAnalyzer;
        mGiniApi = giniApi;
    }

    @Override
    public void sendFeedback(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final GiniVisionNetworkCallback<Void, Error> callback) {
        final DocumentTaskManager documentTaskManager = mGiniApi.getDocumentTaskManager();

        final net.gini.android.models.Document document = mSingleDocumentAnalyzer.getGiniApiDocument();

        // We require the Gini API SDK's net.gini.android.models.Document for sending the feedback
        if (document != null) {
            try {
                documentTaskManager.sendFeedbackForExtractions(document,
                        SpecificExtractionMapper.mapToApiSdk(extractions))
                        .continueWith(new Continuation<net.gini.android.models.Document, Object>() {
                            @Override
                            public Object then(
                                    @NonNull final Task<net.gini.android.models.Document> task)
                                    throws Exception {
                                mUIExecutor.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (task.isFaulted()) {
                                            LOG.error("Feedback error", task.getError());
                                            String message = "unknown";
                                            if (task.getError() != null) {
                                                message = task.getError().getMessage();
                                            }
                                            callback.failure(new Error(message));
                                        } else {
                                            callback.success(null);
                                        }
                                    }
                                });
                                return null;
                            }
                        });
            } catch (final JSONException e) {
                LOG.error("Feedback not sent", e);
                callback.failure(new Error(e.getMessage()));
            }
        } else {
            callback.failure(new Error("Feedback not set: no Gini Api Document available"));
        }
    }

    public static class Builder {
        private SingleDocumentAnalyzer mSingleDocumentAnalyzer;
        private Gini mGiniApi;

        Builder() {
        }

        public Builder withGiniVisionDefaultNetworkService(
                @NonNull final GiniVisionDefaultNetworkService networkService) {
            mSingleDocumentAnalyzer = networkService.getSingleDocumentAnalyzer();
            mGiniApi = networkService.getGiniApi();
            return this;
        }

        public GiniVisionDefaultNetworkApi build() {
            if (mGiniApi == null || mSingleDocumentAnalyzer == null) {
                throw new IllegalStateException("Building requires a Gini and a SingleDocumentAnalyzer instance.");
            }
            return new GiniVisionDefaultNetworkApi(mGiniApi, mSingleDocumentAnalyzer);
        }
    }
}
