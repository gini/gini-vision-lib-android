package net.gini.android.vision.internal.network;

import static com.google.common.truth.Truth.assertThat;

import static net.gini.android.vision.internal.network.GiniVisionNetworkServiceStub.DEFAULT_DOCUMENT_ID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.document.GiniVisionDocumentHelper;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.network.AnalysisResult;
import net.gini.android.vision.network.Error;
import net.gini.android.vision.network.GiniVisionNetworkCallback;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.network.Result;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Alpar Szotyori on 16.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

@RunWith(JUnit4.class)
public class NetworkRequestsManagerTest {

    private GiniVisionNetworkService mGiniVisionNetworkService;

    @Before
    public void setup() {
        mGiniVisionNetworkService = spy(new GiniVisionNetworkServiceStub());
    }

    @Test
    public void should_uploadDocument() throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        // When
        final NetworkRequestResult<GiniVisionDocument> requestResult =
                networkRequestsManager.upload(document).get();
        // Then
        assertThat(requestResult.getApiDocumentId()).isEqualTo(DEFAULT_DOCUMENT_ID);
        assertThat(requestResult.getGiniVisionDocument()).isEqualTo(document);
    }

    @Test
    public void should_throwException_forFailedDocumentUpload() throws Exception {
        // Given
        final String errorMessage = "Something went wrong.";
        final GiniVisionNetworkService networkService = new GiniVisionNetworkServiceStub() {
            @Override
            public void upload(@NonNull final Document document,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                callback.failure(new Error(errorMessage));
            }
        };
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        // When
        ExecutionException exception = null;
        try {
            networkRequestsManager.upload(document).get();
        } catch (final ExecutionException e) {
            exception = e;
        }
        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getCause().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    public void should_allowUploadingSameDocument_afterFailure() throws Exception {
        // Given
        final GiniVisionNetworkService networkService = new GiniVisionNetworkServiceStub() {
            int counter = 0;

            @Override
            public void upload(@NonNull final Document document,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                counter++;
                if (counter == 1) {
                    callback.failure(new Error("Something went wrong."));
                } else {
                    super.upload(document, callback);
                }
            }
        };
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        // When
        ExecutionException firstException = null;
        try {
            networkRequestsManager.upload(document).get();
        } catch (final ExecutionException e) {
            firstException = e;
        }
        final NetworkRequestResult<GiniVisionDocument> secondRequestResult =
                networkRequestsManager.upload(document).get();
        // Then
        assertThat(firstException).isNotNull();
        assertThat(secondRequestResult).isNotNull();
    }

    @Test
    public void should_uploadSameDocument_onlyOnce() throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        // When
        final NetworkRequestResult<GiniVisionDocument> firstRequestResult =
                networkRequestsManager.upload(document).get();
        final NetworkRequestResult<GiniVisionDocument> secondRequestResult =
                networkRequestsManager.upload(document).get();
        // Then
        assertThat(firstRequestResult).isEqualTo(secondRequestResult);
        Mockito.verify(mGiniVisionNetworkService)
                .upload(eq(document), any(GiniVisionNetworkCallback.class));
    }

    @Test
    public void should_deleteDocument() throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        networkRequestsManager.upload(document);
        // When
        final NetworkRequestResult<GiniVisionDocument> requestResult =
                networkRequestsManager.delete(document).get();
        // Then
        assertThat(requestResult.getApiDocumentId()).isEqualTo(DEFAULT_DOCUMENT_ID);
        assertThat(requestResult.getGiniVisionDocument()).isEqualTo(document);
    }

    @Test
    public void should_throwException_forFailedDocumentDeletion() throws Exception {
        // Given
        final String errorMessage = "Something went wrong.";
        final GiniVisionNetworkService networkService = new GiniVisionNetworkServiceStub() {
            @Override
            public void delete(@NonNull final String documentId,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                callback.failure(new Error(errorMessage));
            }
        };
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        networkRequestsManager.upload(document);
        // When
        ExecutionException exception = null;
        try {
            networkRequestsManager.delete(document).get();
        } catch (final ExecutionException e) {
            exception = e;
        }
        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getCause().getMessage()).isEqualTo(errorMessage);
    }

    @Test
    public void should_allowDeletingSameDocument_afterFailure() throws Exception {
        // Given
        final GiniVisionNetworkService networkService = new GiniVisionNetworkServiceStub() {
            int counter = 0;

            @Override
            public void delete(@NonNull final String documentId,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                counter++;
                if (counter == 1) {
                    callback.failure(new Error("Something went wrong."));
                } else {
                    super.delete(documentId, callback);
                }
            }
        };
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        networkRequestsManager.upload(document);
        // When
        ExecutionException firstException = null;
        try {
            networkRequestsManager.delete(document).get();
        } catch (final ExecutionException e) {
            firstException = e;
        }
        final NetworkRequestResult<GiniVisionDocument> secondRequestResult =
                networkRequestsManager.delete(document).get();
        // Then
        assertThat(firstException).isNotNull();
        assertThat(secondRequestResult).isNotNull();
    }

    @Test
    public void should_deleteSameDocument_onlyOnce() throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        networkRequestsManager.upload(document);
        // When
        final NetworkRequestResult<GiniVisionDocument> firstRequestResult =
                networkRequestsManager.delete(document).get();
        final NetworkRequestResult<GiniVisionDocument> secondRequestResult =
                networkRequestsManager.delete(document).get();
        // Then
        assertThat(firstRequestResult).isEqualTo(secondRequestResult);
        Mockito.verify(mGiniVisionNetworkService)
                .delete(eq(firstRequestResult.getApiDocumentId()),
                        any(GiniVisionNetworkCallback.class));
    }

    @Test
    public void should_waitForDocumentRequests_toComplete_beforeDeletingDocument()
            throws Exception {
        // Given
        final Queue<Runnable> uploadCompletionRunnables = new ConcurrentLinkedQueue<>();
        // Simulate upload delays and queue completion to be executed on the main thread
        final GiniVisionNetworkService networkService = spy(new GiniVisionNetworkServiceStub() {
            @Override
            public void upload(@NonNull final Document document,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                final Thread delayThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep((long) (200 + Math.random() * 300));
                            uploadCompletionRunnables.add(new Runnable() {
                                @Override
                                public void run() {
                                    callback.success(new Result(DEFAULT_DOCUMENT_ID));
                                }
                            });
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                delayThread.start();
            }
        });
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        networkRequestsManager.upload(document);
        // When
        // Wait for completion on a secondary thread
        final AtomicReference<NetworkRequestResult<GiniVisionDocument>> requestResult =
                new AtomicReference<>();
        final Thread waitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestResult.set(networkRequestsManager.delete(document).get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        waitThread.start();
        // Run the upload completion runnables on the main thread
        while (waitThread.isAlive()) {
            final Runnable runnable = uploadCompletionRunnables.poll();
            if (runnable != null) {
                runnable.run();
            }
        }
        // Then
        assertThat(requestResult.get()).isNotNull();
        assertThat(requestResult.get().getApiDocumentId()).isEqualTo(DEFAULT_DOCUMENT_ID);
        assertThat(requestResult.get().getGiniVisionDocument()).isEqualTo(document);
        verify(networkService).upload(any(Document.class),
                any(GiniVisionNetworkCallback.class));
        verify(networkService).delete(eq(DEFAULT_DOCUMENT_ID), any(GiniVisionNetworkCallback.class));
    }

    @Test
    public void should_completeDocumentDeletionSuccessfully_whenApiDocumentId_isNotAvailable()
            throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final ImageMultiPageDocument multiPageDocument =
                GiniVisionDocumentHelper.newMultiPageDocument();
        // When
        final NetworkRequestResult<GiniVisionDocument> requestResult =
                networkRequestsManager.delete(multiPageDocument).get();
        // Then
        assertThat(requestResult).isNotNull();
        assertThat(requestResult.getApiDocumentId()).isEqualTo("");
        assertThat(requestResult.getGiniVisionDocument()).isEqualTo(multiPageDocument);
    }

    @Test
    public void should_analyzeMultiPageDocument() throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final ImageMultiPageDocument multiPageDocument =
                GiniVisionDocumentHelper.newMultiPageDocument();
        for (final ImageDocument imageDocument : multiPageDocument.getDocuments()) {
            networkRequestsManager.upload(imageDocument);
        }
        // When
        final AnalysisNetworkRequestResult<GiniVisionMultiPageDocument>
                requestResult = networkRequestsManager.analyze(
                multiPageDocument).get();
        // Then
        assertThat(requestResult.getApiDocumentId()).isEqualTo(DEFAULT_DOCUMENT_ID);
        assertThat(requestResult.getGiniVisionDocument()).isEqualTo(multiPageDocument);
    }

    @Test
    public void should_analyzeSameDocument_onlyOnce() throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final ImageMultiPageDocument multiPageDocument =
                GiniVisionDocumentHelper.newMultiPageDocument();
        for (final ImageDocument imageDocument : multiPageDocument.getDocuments()) {
            networkRequestsManager.upload(imageDocument);
        }
        // When
        final NetworkRequestResult<GiniVisionMultiPageDocument> firstRequestResult =
                networkRequestsManager.analyze(multiPageDocument).get();
        final NetworkRequestResult<GiniVisionMultiPageDocument> secondRequestResult =
                networkRequestsManager.analyze(multiPageDocument).get();
        // Then
        assertThat(firstRequestResult).isEqualTo(secondRequestResult);
        verify(mGiniVisionNetworkService)
                .analyze(any(LinkedHashMap.class),
                        any(GiniVisionNetworkCallback.class));
    }

    @Test
    public void should_waitForPageDocumentUploads_toComplete_beforeAnalyzingMultiPageDocument()
            throws Exception {
        // Given
        final Queue<Runnable> uploadCompletionRunnables = new ConcurrentLinkedQueue<>();
        // Simulate upload delays and queue completion to be executed on the main thread
        final GiniVisionNetworkService networkService = spy(new GiniVisionNetworkServiceStub() {
            @Override
            public void upload(@NonNull final Document document,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                final Thread delayThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep((long) (200 + Math.random() * 300));
                            uploadCompletionRunnables.add(new Runnable() {
                                @Override
                                public void run() {
                                    callback.success(new Result(DEFAULT_DOCUMENT_ID));
                                }
                            });
                        } catch (final InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                delayThread.start();
            }
        });
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final ImageMultiPageDocument multiPageDocument =
                GiniVisionDocumentHelper.newMultiPageDocument();
        // Upload the page documents
        for (final ImageDocument imageDocument : multiPageDocument.getDocuments()) {
            networkRequestsManager.upload(imageDocument);
        }
        // When
        // Wait for completion on a secondary thread
        final AtomicReference<NetworkRequestResult<GiniVisionMultiPageDocument>> requestResult =
                new AtomicReference<>();
        final Thread waitThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    requestResult.set(networkRequestsManager.analyze(multiPageDocument).get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        waitThread.start();
        // Run the upload completion runnables on the main thread
        while (waitThread.isAlive()) {
            final Runnable runnable = uploadCompletionRunnables.poll();
            if (runnable != null) {
                runnable.run();
            }
        }
        // Then
        assertThat(requestResult.get()).isNotNull();
        assertThat(requestResult.get().getApiDocumentId()).isEqualTo(DEFAULT_DOCUMENT_ID);
        assertThat(requestResult.get().getGiniVisionDocument()).isEqualTo(multiPageDocument);
        verify(networkService, times(3)).upload(any(Document.class),
                any(GiniVisionNetworkCallback.class));
    }


    @Test
    public void should_cancelDocumentRequests() throws Exception {
        // Given
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(mGiniVisionNetworkService);
        final ImageDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        // When
        networkRequestsManager.cancel(document);
        // Then
        verify(mGiniVisionNetworkService).cancel(document);
    }

    @Test
    public void should_allowUploadingSameDocument_afterCancellation() throws Exception {
        // Given
        final GiniVisionNetworkService networkService = spy(new GiniVisionNetworkServiceStub() {
            @Override
            public void upload(@NonNull final Document document,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                // Cancellation
                callback.cancelled();
            }
        });
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        // When
        networkRequestsManager.upload(document);
        networkRequestsManager.cancel(document);
        networkRequestsManager.upload(document);
        // Then
        verify(networkService, times(2)).upload(eq(document),
                any(GiniVisionNetworkCallback.class));
    }

    @Test
    public void should_allowDeletingSameDocument_afterCancelation() throws Exception {
        // Given
        final GiniVisionNetworkService networkService = spy(new GiniVisionNetworkServiceStub() {
            @Override
            public void delete(@NonNull final String documentId,
                    @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
                // Cancellation
                callback.cancelled();
            }
        });
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final GiniVisionDocument document = GiniVisionDocumentHelper.newEmptyImageDocument();
        // When
        networkRequestsManager.upload(document);
        networkRequestsManager.delete(document);
        networkRequestsManager.cancel(document);
        networkRequestsManager.delete(document);
        // Then
        verify(networkService, times(2)).delete(any(String.class),
                any(GiniVisionNetworkCallback.class));
    }

    @Test
    public void should_allowAnalyzingSameDocument_afterCancelation() throws Exception {
        // Given
        final GiniVisionNetworkService networkService = spy(new GiniVisionNetworkServiceStub() {
            @Override
            public void analyze(@NonNull final LinkedHashMap<String, Integer> documentIdRotationMap,
                    @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
                // Cancellation
                callback.cancelled();
            }
        });
        final NetworkRequestsManager networkRequestsManager =
                new NetworkRequestsManager(networkService);
        final ImageMultiPageDocument multiPageDocument =
                GiniVisionDocumentHelper.newMultiPageDocument();
        for (final ImageDocument imageDocument : multiPageDocument.getDocuments()) {
            networkRequestsManager.upload(imageDocument);
        }
        // When
        networkRequestsManager.analyze(multiPageDocument);
        networkRequestsManager.cancel(multiPageDocument);
        networkRequestsManager.analyze(multiPageDocument);
        // Then
        verify(networkService, times(2)).analyze(any(LinkedHashMap.class),
                any(GiniVisionNetworkCallback.class));
    }
}