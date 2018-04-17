package net.gini.android.vision.internal.network;

/**
 * Created by Alpar Szotyori on 13.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import android.support.annotation.NonNull;

import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.network.AnalysisResult;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetworkCallback;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * @exclude
 */
public class NetworkRequestManager {

    private final Map<GiniVisionDocument, String> mDocumentApiDocumentIdMap;
    private final Map<GiniVisionDocument, CompletableFuture<NetworkRequestResult<GiniVisionDocument>>>
            mDocumentUploadFutureMap;
    private final Map<GiniVisionDocument, CompletableFuture<NetworkRequestResult<GiniVisionDocument>>>
            mDocumentDeleteFutureMap;
    private final Map<GiniVisionDocument, CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>>
            mDocumentAnalyzeFutureMap;

    private final GiniVisionNetworkService mGiniVisionNetworkService;

    public NetworkRequestManager(
            final GiniVisionNetworkService giniVisionNetworkService) {
        mGiniVisionNetworkService = giniVisionNetworkService;
        mDocumentApiDocumentIdMap = new HashMap<>();
        mDocumentUploadFutureMap = new HashMap<>();
        mDocumentDeleteFutureMap = new HashMap<>();
        mDocumentAnalyzeFutureMap = new HashMap<>();
    }

    public CompletableFuture<NetworkRequestResult<GiniVisionDocument>> upload(
            @NonNull final GiniVisionDocument document) {
        final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> documentUploadFuture =
                mDocumentUploadFutureMap.get(document);
        if (documentUploadFuture != null) {
            return documentUploadFuture;
        }

        final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> future = new CompletableFuture<>();
        mDocumentUploadFutureMap.put(document, future);

        mGiniVisionNetworkService.upload(document, new GiniVisionNetworkCallback<Result, Error>() {
            @Override
            public void failure(final Error error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }

            @Override
            public void success(final Result result) {
                mDocumentApiDocumentIdMap.put(document, result.getDocumentId());
                future.complete(new NetworkRequestResult<>(document, result.getDocumentId()));
            }

            @Override
            public void cancelled() {
                future.cancel(false);
            }
        });

        future.handle(
                new CompletableFuture.BiFun<NetworkRequestResult<GiniVisionDocument>, Throwable, NetworkRequestResult<GiniVisionDocument>>() {
                    @Override
                    public NetworkRequestResult<GiniVisionDocument> apply(
                            final NetworkRequestResult<GiniVisionDocument> networkRequestResult,
                            final Throwable throwable) {
                        if (throwable != null) {
                            mDocumentUploadFutureMap.remove(document);
                        }
                        return networkRequestResult;
                    }
                });
        return future;
    }

    public CompletableFuture<NetworkRequestResult<GiniVisionDocument>> delete(
            @NonNull final GiniVisionDocument document) {
        final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> documentDeleteFuture =
                mDocumentDeleteFutureMap.get(document);
        if (documentDeleteFuture != null) {
            return documentDeleteFuture;
        }

        final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> future = new CompletableFuture<>();

        final String apiDocumentId = mDocumentApiDocumentIdMap.get(document);
        if (apiDocumentId == null) {
            future.complete(new NetworkRequestResult<>(document, ""));
            return future;
        }

        mDocumentDeleteFutureMap.put(document, future);

        mGiniVisionNetworkService.delete(apiDocumentId,
                new GiniVisionNetworkCallback<Result, Error>() {
                    @Override
                    public void failure(final Error error) {
                        future.completeExceptionally(new RuntimeException(error.getMessage()));
                    }

                    @Override
                    public void success(final Result result) {
                        mDocumentApiDocumentIdMap.remove(document);
                        future.complete(new NetworkRequestResult<>(document, result.getDocumentId()));
                    }

                    @Override
                    public void cancelled() {
                        future.cancel(false);
                    }
                });


        future.handle(
                new CompletableFuture.BiFun<NetworkRequestResult<GiniVisionDocument>, Throwable, NetworkRequestResult<GiniVisionDocument>>() {
                    @Override
                    public NetworkRequestResult<GiniVisionDocument> apply(
                            final NetworkRequestResult<GiniVisionDocument> networkRequestResult,
                            final Throwable throwable) {
                        if (throwable != null) {
                            mDocumentDeleteFutureMap.remove(document);
                        }
                        return networkRequestResult;
                    }
                });
        return future;
    }

    public CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>> analyze(
            @NonNull final GiniVisionMultiPageDocument multiPageDocument) {
        final CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>
                documentAnalyzeFuture =
                mDocumentAnalyzeFutureMap.get(multiPageDocument);
        if (documentAnalyzeFuture != null) {
            return documentAnalyzeFuture;
        }

            final List<CompletableFuture> documentFutures = new ArrayList<>();
            for (final Object document : multiPageDocument.getDocuments()) {
                final GiniVisionDocument giniVisionDocument = (GiniVisionDocument) document;
                final CompletableFuture documentFuture = mDocumentUploadFutureMap.get(
                        giniVisionDocument);
                if (documentFuture != null) {
                    documentFutures.add(documentFuture);
                }
            }

            return CompletableFuture
                    .allOf(documentFutures.toArray(new CompletableFuture[documentFutures.size()]))
                    .thenCompose(
                            new CompletableFuture.Fun<Void, CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>>() {
                                @Override
                                public CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>> apply(
                                        final Void aVoid) {
                                    final CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>
                                            documentAnalyzeFuture =
                                            mDocumentAnalyzeFutureMap.get(multiPageDocument);
                                    if (documentAnalyzeFuture != null) {
                                        return documentAnalyzeFuture;
                                    }

                                    final CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>
                                            future =
                                            new CompletableFuture<>();
                                    mDocumentAnalyzeFutureMap.put(multiPageDocument, future);

                                    final LinkedHashMap<String, Integer> apiDocumentIdRotationMap =
                                            new LinkedHashMap<>();
                                    for (final Object document : multiPageDocument.getDocuments()) {
                                        final GiniVisionDocument giniVisionDocument = (GiniVisionDocument) document;
                                        final String apiDocumentId = mDocumentApiDocumentIdMap.get(
                                                giniVisionDocument);
                                        if (apiDocumentId != null) {
                                            int rotationForDisplay = 0;
                                            if (giniVisionDocument instanceof ImageDocument) {
                                                rotationForDisplay =
                                                        ((ImageDocument) giniVisionDocument).getRotationForDisplay();
                                            }
                                            apiDocumentIdRotationMap.put(apiDocumentId, rotationForDisplay);
                                        } else {
                                            throw new IllegalStateException(
                                                    "Missing partial document id. All page documents of a multi-page document have to be uploaded before analyzing the multi-page document.");
                                        }
                                    }

                                    mGiniVisionNetworkService.analyze(apiDocumentIdRotationMap,
                                            new GiniVisionNetworkCallback<AnalysisResult, Error>() {
                                                @Override
                                                public void failure(final Error error) {
                                                    future.completeExceptionally(
                                                            new RuntimeException(
                                                                    error.getMessage()));
                                                }

                                                @Override
                                                public void success(final AnalysisResult result) {
                                                    future.complete(new AnalysisNetworkRequestResult<>(
                                                            multiPageDocument,
                                                            result.getDocumentId(),
                                                            result));
                                                }

                                                @Override
                                                public void cancelled() {
                                                    future.cancel(false);
                                                }
                                            });

                                    future.handle(
                                            new CompletableFuture.BiFun<NetworkRequestResult<GiniVisionMultiPageDocument>, Throwable, NetworkRequestResult<GiniVisionMultiPageDocument>>() {
                                                @Override
                                                public NetworkRequestResult<GiniVisionMultiPageDocument> apply(
                                                        final NetworkRequestResult<GiniVisionMultiPageDocument> networkRequestResult,
                                                        final Throwable throwable) {
                                                    if (throwable != null) {
                                                        mDocumentAnalyzeFutureMap.remove(
                                                                multiPageDocument);
                                                    }
                                                    return networkRequestResult;
                                                }
                                            });
                                    return future;
                                }
                            });
    }

    public void cancel(@NonNull final GiniVisionDocument document) {
        mGiniVisionNetworkService.cancel(document);
        mDocumentUploadFutureMap.remove(document);
        mDocumentAnalyzeFutureMap.remove(document);
        mDocumentDeleteFutureMap.remove(document);
    }

    public void cancelAll() {
        mGiniVisionNetworkService.cancelAll();
        mDocumentUploadFutureMap.clear();
        mDocumentAnalyzeFutureMap.clear();
        mDocumentDeleteFutureMap.clear();
        mDocumentApiDocumentIdMap.clear();
    }

}
