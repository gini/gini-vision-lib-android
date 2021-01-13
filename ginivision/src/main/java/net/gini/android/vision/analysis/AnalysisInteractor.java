package net.gini.android.vision.analysis;

import static net.gini.android.vision.internal.network.NetworkRequestsManager.isCancellation;

import android.app.Application;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.internal.network.AnalysisNetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestResult;
import net.gini.android.vision.internal.network.NetworkRequestsManager;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 09.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class AnalysisInteractor {

    private final Application mApp;

    public AnalysisInteractor(@NonNull final Application app) {
        mApp = app;
    }

    public CompletableFuture<ResultHolder> analyzeMultiPageDocument(
            final GiniVisionMultiPageDocument<GiniVisionDocument, GiniVisionDocumentError>
                    multiPageDocument) {
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                GiniVisionDebug.writeDocumentToFile(mApp, multiPageDocument, "_for_analysis");
                for (final Object document : multiPageDocument.getDocuments()) {
                    final GiniVisionDocument giniVisionDocument = (GiniVisionDocument) document;
                    networkRequestsManager.upload(mApp, giniVisionDocument);
                }
                return networkRequestsManager.analyze(multiPageDocument)
                        .handle(new CompletableFuture.BiFun<AnalysisNetworkRequestResult<
                                GiniVisionMultiPageDocument>, Throwable, ResultHolder>() {
                            @Override
                            public ResultHolder apply(
                                    final AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>
                                            requestResult,
                                    final Throwable throwable) {
                                if (throwable != null && !isCancellation(throwable)) {
                                    throw new RuntimeException(throwable); // NOPMD
                                } else if (requestResult != null) {
                                    final Map<String, GiniVisionSpecificExtraction> extractions =
                                            requestResult.getAnalysisResult().getExtractions();
                                    if (extractions.isEmpty()) {
                                        return new ResultHolder(Result.SUCCESS_NO_EXTRACTIONS);
                                    } else {
                                        return new ResultHolder(Result.SUCCESS_WITH_EXTRACTIONS,
                                                extractions);
                                    }
                                }
                                return null;
                            }
                        });
            } else {
                return CompletableFuture.completedFuture(
                        new ResultHolder(Result.NO_NETWORK_SERVICE));
            }
        } else {
            return CompletableFuture.completedFuture(new ResultHolder(Result.NO_NETWORK_SERVICE));
        }
    }

    public CompletableFuture<Void> deleteMultiPageDocument(
            final GiniVisionMultiPageDocument<GiniVisionDocument, GiniVisionDocumentError>
                    multiPageDocument) {
        return deleteDocument(multiPageDocument)
                .handle(new CompletableFuture.BiFun<NetworkRequestResult<GiniVisionDocument>,
                        Throwable, Void>() {
                    @Override
                    public Void apply(
                            final NetworkRequestResult<GiniVisionDocument>
                                    giniVisionDocumentNetworkRequestResult,
                            final Throwable throwable) {
                        return null;
                    }
                })
                .thenCompose(
                        new CompletableFuture.Fun<Void, CompletableFuture<Void>>() {
                            @Override
                            public CompletableFuture<Void> apply(
                                    final Void result) {
                                final NetworkRequestsManager networkRequestsManager =
                                        GiniVision.getInstance()
                                                .internal().getNetworkRequestsManager();
                                if (networkRequestsManager == null) {
                                    return CompletableFuture.completedFuture(null);
                                }
                                final List<CompletableFuture<NetworkRequestResult<
                                        GiniVisionDocument>>> futures = new ArrayList<>();
                                for (final GiniVisionDocument document
                                        : multiPageDocument.getDocuments()) {
                                    networkRequestsManager.cancel(document);
                                    futures.add(networkRequestsManager.delete(document));
                                }
                                return CompletableFuture.allOf(
                                        futures.toArray(new CompletableFuture[0]));
                            }
                        });
    }

    public CompletableFuture<NetworkRequestResult<GiniVisionDocument>> deleteDocument(
            final GiniVisionDocument document) {
        if (GiniVision.hasInstance()) {
            final NetworkRequestsManager networkRequestsManager = GiniVision.getInstance()
                    .internal().getNetworkRequestsManager();
            if (networkRequestsManager != null) {
                networkRequestsManager.cancel(document);
                return networkRequestsManager.delete(document);
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public enum Result {
        SUCCESS_NO_EXTRACTIONS,
        SUCCESS_WITH_EXTRACTIONS,
        NO_NETWORK_SERVICE
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final class ResultHolder {

        private final Result mResult;
        private final Map<String, GiniVisionSpecificExtraction> mExtractions;

        ResultHolder(@NonNull final Result result) {
            this(result, Collections.<String, GiniVisionSpecificExtraction>emptyMap());
        }

        ResultHolder(
                @NonNull final Result result,
                @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
            mResult = result;
            mExtractions = extractions;
        }

        @NonNull
        public Result getResult() {
            return mResult;
        }

        @NonNull
        public Map<String, GiniVisionSpecificExtraction> getExtractions() {
            return mExtractions;
        }
    }
}
