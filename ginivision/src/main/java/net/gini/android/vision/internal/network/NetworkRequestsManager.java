package net.gini.android.vision.internal.network;

/**
 * Created by Alpar Szotyori on 13.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.network.AnalysisResult;
import net.gini.android.vision.network.CancellationToken;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetworkCallback;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import jersey.repackaged.jsr166e.CompletableFuture;
import jersey.repackaged.jsr166e.CompletionException;

/**
 * @exclude
 */
public class NetworkRequestsManager {

    private final Map<String, String> mApiDocumentIds;
    private final Map<String, CompletableFuture<NetworkRequestResult<GiniVisionDocument>>>
            mDocumentUploadFutures;
    private final Map<String, CompletableFuture<NetworkRequestResult<GiniVisionDocument>>>
            mDocumentDeleteFutures;
    private final Map<String, CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>>
            mDocumentAnalyzeFutures;

    private final GiniVisionNetworkService mGiniVisionNetworkService;

    public static String getErrorMessage(@NonNull final Throwable throwable) {
        if (throwable instanceof CompletionException) {
            return throwable.getCause().getMessage();
        }
        return throwable.getMessage();
    }

    public static boolean isCancellation(@NonNull final Throwable throwable) {
        return throwable instanceof CancellationException;
    }

    public NetworkRequestsManager(
            final GiniVisionNetworkService giniVisionNetworkService) {
        mGiniVisionNetworkService = giniVisionNetworkService;
        mApiDocumentIds = new HashMap<>();
        mDocumentUploadFutures = new HashMap<>();
        mDocumentDeleteFutures = new HashMap<>();
        mDocumentAnalyzeFutures = new HashMap<>();
    }

    public CompletableFuture<NetworkRequestResult<GiniVisionDocument>> upload(
            @NonNull final GiniVisionDocument document) {
        final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> documentUploadFuture =
                mDocumentUploadFutures.get(document.getId());
        if (documentUploadFuture != null) {
            return documentUploadFuture;
        }

        final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> future =
                new CompletableFuture<>();
        mDocumentUploadFutures.put(document.getId(), future);

        final CancellationToken cancellationToken =
                mGiniVisionNetworkService.upload(document, new GiniVisionNetworkCallback<Result, Error>() {
            @Override
            public void failure(final Error error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }

            @Override
            public void success(final Result result) {
                mApiDocumentIds.put(document.getId(), result.getDocumentId());
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
                            if (isCancellation(throwable)) {
                                cancellationToken.cancel();
                            }
                            mDocumentUploadFutures.remove(document.getId());
                        }
                        return networkRequestResult;
                    }
                });
        return future;
    }

    public CompletableFuture<NetworkRequestResult<GiniVisionDocument>> delete(
            @NonNull final GiniVisionDocument document) {
        final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> documentDeleteFuture =
                mDocumentDeleteFutures.get(document.getId());
        if (documentDeleteFuture != null) {
            return documentDeleteFuture;
        }

        final List<CompletableFuture> documentFutures = new ArrayList<>();
        final CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>
                analyzeFuture = mDocumentAnalyzeFutures.get(document.getId());
        if (analyzeFuture != null) {
            documentFutures.add(analyzeFuture);
        }
        if (document instanceof GiniVisionMultiPageDocument) {
            final GiniVisionMultiPageDocument multiPageDocument =
                    (GiniVisionMultiPageDocument) document;
            for (final Object partialDocument : multiPageDocument.getDocuments()) {
                final GiniVisionDocument giniVisionDocument = (GiniVisionDocument) partialDocument;
                final CompletableFuture<NetworkRequestResult<GiniVisionDocument>>
                        uploadFuture = mDocumentUploadFutures.get(giniVisionDocument.getId());
                if (uploadFuture != null) {
                    documentFutures.add(uploadFuture);
                }
            }
        } else {
            final CompletableFuture<NetworkRequestResult<GiniVisionDocument>>
                    uploadFuture = mDocumentUploadFutures.get(document.getId());
            if (uploadFuture != null) {
                documentFutures.add(uploadFuture);
            }
        }

        return CompletableFuture
                .allOf(documentFutures.toArray(new CompletableFuture[documentFutures.size()]))
                .handle(new CompletableFuture.BiFun<Void, Throwable, Boolean>() {
                    @Override
                    public Boolean apply(final Void aVoid, final Throwable throwable) {
                        if (throwable != null) {
                            return false;
                        }
                        return true;
                    }
                })
                .thenCompose(
                        new CompletableFuture.Fun<Boolean, CompletableFuture<NetworkRequestResult<GiniVisionDocument>>>() {
                            @Override
                            public CompletableFuture<NetworkRequestResult<GiniVisionDocument>> apply(
                                    final Boolean success) {
                                final CompletableFuture<NetworkRequestResult<GiniVisionDocument>> documentDeleteFuture =
                                        mDocumentDeleteFutures.get(document.getId());
                                if (documentDeleteFuture != null) {
                                    return documentDeleteFuture;
                                }

                                final CompletableFuture<NetworkRequestResult<GiniVisionDocument>>
                                        future = new CompletableFuture<>();
                                mDocumentDeleteFutures.put(document.getId(), future);

                                final String apiDocumentId = mApiDocumentIds.get(document.getId());
                                if (apiDocumentId == null) {
                                    future.complete(new NetworkRequestResult<>(document, ""));
                                    return future;
                                }
                                if (!success) {
                                    future.complete(
                                            new NetworkRequestResult<>(document, apiDocumentId));
                                    return future;
                                }

                                final CancellationToken cancellationToken =
                                        mGiniVisionNetworkService.delete(apiDocumentId,
                                        new GiniVisionNetworkCallback<Result, Error>() {
                                            @Override
                                            public void failure(final Error error) {
                                                future.completeExceptionally(
                                                        new RuntimeException(error.getMessage()));
                                            }

                                            @Override
                                            public void success(final Result result) {
                                                mApiDocumentIds.remove(document.getId());
                                                future.complete(
                                                        new NetworkRequestResult<>(document,
                                                                result.getDocumentId()));
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
                                                    if (isCancellation(throwable)) {
                                                        cancellationToken.cancel();
                                                    }
                                                    mDocumentDeleteFutures.remove(document.getId());
                                                }
                                                return networkRequestResult;
                                            }
                                        });

                                return future;
                            }
                        });
    }

    public CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>> analyze(
            @NonNull final GiniVisionMultiPageDocument multiPageDocument) {
        final CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>
                documentAnalyzeFuture =
                mDocumentAnalyzeFutures.get(multiPageDocument.getId());
        if (documentAnalyzeFuture != null) {
            return documentAnalyzeFuture;
        }

        final List<CompletableFuture> documentFutures = new ArrayList<>();
        for (final Object document : multiPageDocument.getDocuments()) {
            final GiniVisionDocument giniVisionDocument = (GiniVisionDocument) document;
            final CompletableFuture documentFuture = mDocumentUploadFutures.get(
                    giniVisionDocument.getId());
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
                                        mDocumentAnalyzeFutures.get(multiPageDocument.getId());
                                if (documentAnalyzeFuture != null) {
                                    return documentAnalyzeFuture;
                                }

                                final CompletableFuture<AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>>
                                        future =
                                        new CompletableFuture<>();
                                mDocumentAnalyzeFutures.put(multiPageDocument.getId(), future);

                                final LinkedHashMap<String, Integer> documentRotationDeltas =
                                        new LinkedHashMap<>();
                                for (final Object document : multiPageDocument.getDocuments()) {
                                    final GiniVisionDocument giniVisionDocument =
                                            (GiniVisionDocument) document;
                                    final String apiDocumentId = mApiDocumentIds.get(
                                            giniVisionDocument.getId());
                                    if (apiDocumentId != null) {
                                        int rotationDelta = 0;
                                        if (giniVisionDocument instanceof ImageDocument) {
                                            rotationDelta =
                                                    ((ImageDocument) giniVisionDocument).getRotationDelta();
                                        }
                                        documentRotationDeltas.put(apiDocumentId, rotationDelta);
                                    } else {
                                        throw new IllegalStateException(
                                                "Missing partial document id. All page documents of a multi-page document have to be uploaded before analysis.");
                                    }
                                }

                                final CancellationToken cancellationToken =
                                        mGiniVisionNetworkService.analyze(documentRotationDeltas,
                                        new GiniVisionNetworkCallback<AnalysisResult, Error>() {
                                            @Override
                                            public void failure(final Error error) {
                                                future.completeExceptionally(
                                                        new RuntimeException(
                                                                error.getMessage()));
                                            }

                                            @Override
                                            public void success(final AnalysisResult result) {
                                                mApiDocumentIds.put(multiPageDocument.getId(), result.getDocumentId());
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
                                                    if (isCancellation(throwable)) {
                                                        cancellationToken.cancel();
                                                    }
                                                    mDocumentAnalyzeFutures.remove(
                                                            multiPageDocument.getId());
                                                }
                                                return networkRequestResult;
                                            }
                                        });
                                return future;
                            }
                        });
    }

    public void cancel(@NonNull final GiniVisionDocument document) {
        cancelFuture(mDocumentUploadFutures.get(document.getId()));
        cancelFuture(mDocumentAnalyzeFutures.get(document.getId()));
        cancelFuture(mDocumentDeleteFutures.get(document.getId()));
    }

    private void cancelFuture(@Nullable final CompletableFuture future) {
        if (future != null) {
            future.cancel(false);
        }
    }

    public void cancelAll() {
        for (final CompletableFuture future : mDocumentUploadFutures.values()) {
            cancelFuture(future);
        }
        for (final CompletableFuture future : mDocumentAnalyzeFutures.values()) {
            cancelFuture(future);
        }
        for (final CompletableFuture future : mDocumentDeleteFutures.values()) {
            cancelFuture(future);
        }
    }

    public void reset() {
        cancelAll();
        mApiDocumentIds.clear();
    }

}
