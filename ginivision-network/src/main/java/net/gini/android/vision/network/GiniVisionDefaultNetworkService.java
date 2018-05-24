package net.gini.android.vision.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.android.volley.Cache;

import net.gini.android.Gini;
import net.gini.android.SdkBuilder;
import net.gini.android.authorization.CredentialsStore;
import net.gini.android.authorization.SessionManager;
import net.gini.android.vision.Document;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.network.model.GiniVisionExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.util.CancellationToken;
import net.gini.android.vision.util.NoOpCancellationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import bolts.Task;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionDefaultNetworkService implements GiniVisionNetworkService {

    private static final Logger LOG = LoggerFactory.getLogger(
            GiniVisionDefaultNetworkService.class);

    private final Gini mGiniApi;
    private final Map<String, net.gini.android.models.Document> mGiniApiDocuments = new HashMap<>();
    private net.gini.android.models.Document mAnalyzedGiniApiDocument;

    public static Builder builder(@NonNull final Context context) {
        return new Builder(context);
    }

    GiniVisionDefaultNetworkService(@NonNull final Gini giniApi) {
        mGiniApi = giniApi;
    }

    @Override
    public CancellationToken upload(@NonNull final Document document,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        LOG.debug("Upload document {}", document.getId());
        if (document.getData() == null) {
            final Error error = new Error("Document has no data. Did you forget to load it?");
            LOG.error("Document upload failed for {}: {}", document.getId(), error.getMessage());
            callback.failure(error);
            return new NoOpCancellationToken();
        }
        if (document instanceof GiniVisionMultiPageDocument) {
            final Error error = new Error(
                    "Multi-page document cannot be uploaded. You have to upload each of its page documents separately.");
            LOG.error("Document upload failed for {}: {}", document.getId(), error.getMessage());
            callback.failure(error);
            return new NoOpCancellationToken();
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                final net.gini.android.models.Document apiDocument =
                        new net.gini.android.models.Document(
                                UUID.randomUUID().toString(),
                                net.gini.android.models.Document.ProcessingState.PENDING, "", 1,
                                new Date(),
                                net.gini.android.models.Document.SourceClassification.SCANNED, null,
                                null, null);
                LOG.debug("Document upload success for {}: api document id {}", document.getId(),
                        apiDocument.getId());
                mGiniApiDocuments.put(apiDocument.getId(), apiDocument);
                if (Math.random() >= 0.5) {
                    callback.success(new Result(apiDocument.getId()));
                } else {
                    callback.failure(new Error("Something went wrong."));
                }
            }
//        }, 4000 + (Math.round(Math.random() * 3000)));
        }, 500);


//        mGiniApi.getDocumentTaskManager()
//                .createPartialDocument(document.getData(), document.getMimeType(), null, null)
//                .continueWith(new Continuation<net.gini.android.models.Document, Void>() {
//                    @Override
//                    public Void then(final Task<net.gini.android.models.Document> task)
//                            throws Exception {
//                        if (task.isFaulted()) {
//                            final Error error = new Error(getTaskErrorMessage(task));
//                            LOG.error("Document upload failed for {}: {}", document.getId(),
//                                    error.getMessage());
//                            callback.failure(error);
//                        } else if (task.getResult() != null) {
//                            final net.gini.android.models.Document apiDocument = task.getResult();
//                            LOG.debug("Document upload success for {}: {}", document.getId(),
//                                    apiDocument);
//                            mGiniApiDocuments.put(apiDocument.getId(), apiDocument);
//                            callback.success(new Result(apiDocument.getId()));
//                        } else {
//                            LOG.debug("Document upload cancelled for {}", document.getId());
//                            callback.cancelled();
//                        }
//                        return null;
//                    }
//                }, Task.UI_THREAD_EXECUTOR);
        return new NoOpCancellationToken();
    }

    @Override
    public CancellationToken delete(@NonNull final String giniApiDocumentId,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        LOG.debug("Delete document with api id {}", giniApiDocumentId);

        LOG.debug("Document deletion success for api id {}", giniApiDocumentId);
        callback.success(new Result(giniApiDocumentId));
//        mGiniApi.getDocumentTaskManager().deletePartialDocumentAndParents(giniApiDocumentId)
//                .continueWith(new Continuation<String, Void>() {
//                    @Override
//                    public Void then(final Task<String> task) throws Exception {
//                        if (task.isFaulted()) {
//                            final Error error = new Error(getTaskErrorMessage(task));
//                            LOG.error("Document deletion failed for api id {}: {}",
//                                    giniApiDocumentId,
//                                    error.getMessage());
//                            callback.failure(error);
//                        } else if (task.getResult() != null) {
//                            LOG.debug("Document deletion success for api id {}", giniApiDocumentId);
//                            callback.success(new Result(giniApiDocumentId));
//                        } else {
//                            LOG.debug("Document deletion cancelled for api id {}",
//                                    giniApiDocumentId);
//                            callback.cancelled();
//                        }
//                        return null;
//                    }
//                }, Task.UI_THREAD_EXECUTOR);
        return new NoOpCancellationToken();
    }

    @Override
    public CancellationToken analyze(
            @NonNull final LinkedHashMap<String, Integer> giniApiDocumentIdRotationMap,
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
        LOG.debug("Analyze documents {}", giniApiDocumentIdRotationMap);
        final LinkedHashMap<net.gini.android.models.Document, Integer> giniApiDocumentRotationMap =
                new LinkedHashMap<>();
        final boolean success = collectGiniApiDocuments(giniApiDocumentRotationMap,
                giniApiDocumentIdRotationMap, callback);
        if (!success) {
            return new NoOpCancellationToken();
        }
        mAnalyzedGiniApiDocument = null;
        final AtomicBoolean isCancelled = new AtomicBoolean();
        final AtomicReference<net.gini.android.models.Document> compositeDocument =
                new AtomicReference<>();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isCancelled.get()) {
                    LOG.debug("Document analysis cancelled for documents {}",
                            giniApiDocumentIdRotationMap);
                    callback.cancelled();
                    return;
                }
                final net.gini.android.models.Document giniApiDocument =
                        new net.gini.android.models.Document(
                                UUID.randomUUID().toString(),
                                net.gini.android.models.Document.ProcessingState.PENDING, "", 1,
                                new Date(),
                                net.gini.android.models.Document.SourceClassification.COMPOSITE,
                                null, null, null);
                // Composite document needed to create the AnalysisResult later
                mGiniApiDocuments.put(giniApiDocument.getId(), giniApiDocument);
                mAnalyzedGiniApiDocument = giniApiDocument;
                final Map<String, GiniVisionSpecificExtraction> extractions = new HashMap<>();
                extractions.put("amountToPay",
                        new GiniVisionSpecificExtraction("amountToPay", "10.00:EUR", "", null,
                                Collections.singletonList(
                                        new GiniVisionExtraction("10.00:EUR", "amountToPay",
                                                null))));
                LOG.debug("Document analysis success for documents {}: {}",
                        giniApiDocumentIdRotationMap, extractions);
                callback.success(
                        new AnalysisResult(giniApiDocument.getId(), extractions));
            }
        }, 5000);


//        mGiniApi.getDocumentTaskManager().createCompositeDocument(giniApiDocumentRotationMap, null)
//                .onSuccessTask(
//                        new Continuation<net.gini.android.models.Document, Task<net.gini.android.models.Document>>() {
//                            @Override
//                            public Task<net.gini.android.models.Document> then(
//                                    final Task<net.gini.android.models.Document> task)
//                                    throws Exception {
//                                if (isCancelled.get()) {
//                                    LOG.debug(
//                                            "Document analysis cancelled after composite document creation for documents {}",
//                                            giniApiDocumentIdRotationMap);
//                                    return Task.cancelled();
//                                }
//                                if (task.isCancelled()) {
//                                    LOG.debug(
//                                            "Composite document creation cancelled for documents {}",
//                                            giniApiDocumentIdRotationMap);
//                                    return task;
//                                }
//                                final net.gini.android.models.Document giniApiDocument =
//                                        task.getResult();
//                                // Composite document needed to create the AnalysisResult later
//                                compositeDocument.set(giniApiDocument);
//                                mGiniApiDocuments.put(giniApiDocument.getId(), giniApiDocument);
//                                return mGiniApi.getDocumentTaskManager().pollDocument(giniApiDocument);
//                            }
//                        })
//                .onSuccessTask(
//                        new Continuation<net.gini.android.models.Document, Task<Map<String, SpecificExtraction>>>() {
//                            @Override
//                            public Task<Map<String, SpecificExtraction>> then(
//                                    final Task<net.gini.android.models.Document> task)
//                                    throws Exception {
//                                if (isCancelled.get()) {
//                                    LOG.debug(
//                                            "Document analysis cancelled after polling for documents {}",
//                                            giniApiDocumentIdRotationMap);
//                                    return Task.cancelled();
//                                }
//                                final net.gini.android.models.Document giniApiDocument =
//                                        task.getResult();
//                                if (task.isCancelled()) {
//                                    LOG.debug(
//                                            "Composite document polling cancelled for documents {}",
//                                            giniApiDocumentIdRotationMap);
//                                    return Task.cancelled();
//                                }
//                                return mGiniApi.getDocumentTaskManager().getExtractions(
//                                        giniApiDocument);
//                            }
//                        })
//                .continueWith(
//                        new Continuation<Map<String, SpecificExtraction>, Void>() {
//                            @Override
//                            public Void then(
//                                    final Task<Map<String, SpecificExtraction>> task)
//                                    throws Exception {
//                                if (task.isFaulted()) {
//                                    final Error error = new Error(getTaskErrorMessage(task));
//                                    LOG.error("Document analysis failed for documents {}: {}",
//                                            giniApiDocumentIdRotationMap, error.getMessage());
//                                    callback.failure(error);
//                                } else if (task.getResult() != null) {
//                                    mAnalyzedGiniApiDocument = compositeDocument.get();
//                                    final Map<String, GiniVisionSpecificExtraction> extractions =
//                                            SpecificExtractionMapper.mapToGVL(task.getResult());
//                                    LOG.debug("Document analysis success for documents {}: {}",
//                                            giniApiDocumentIdRotationMap, extractions);
//                                    callback.success(
//                                            new AnalysisResult(compositeDocument.get().getId(),
//                                                    extractions));
//                                } else {
//                                    LOG.debug("Document analysis cancelled for documents {}",
//                                            giniApiDocumentIdRotationMap);
//                                    callback.cancelled();
//                                }
//                                return null;
//                            }
//                        }, Task.UI_THREAD_EXECUTOR);
        return new CancellationToken() {
            @Override
            public void cancel() {
                LOG.debug("Document analaysis cancellation requested for documents {}",
                        giniApiDocumentIdRotationMap);
                isCancelled.set(true);
                if (compositeDocument.get() != null) {
                    mGiniApi.getDocumentTaskManager().cancelDocumentPolling(
                            compositeDocument.get());
                }
            }
        };

    }

    @Override
    public void cleanup() {
        mAnalyzedGiniApiDocument = null;
        mGiniApiDocuments.clear();
    }

    private boolean collectGiniApiDocuments(
            @NonNull final LinkedHashMap<net.gini.android.models.Document, Integer> giniApiDocumentRotationMap,
            @NonNull final LinkedHashMap<String, Integer> giniApiDocumentIdRotationMap,
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
        for (final Map.Entry<String, Integer> entry : giniApiDocumentIdRotationMap.entrySet()) {
            final net.gini.android.models.Document document = mGiniApiDocuments.get(entry.getKey());
            if (document == null) {
                final Error error = new Error("Missing partial document.");
                LOG.error("Document analysis failed for documents {}: {}",
                        giniApiDocumentIdRotationMap,
                        error.getMessage());
                callback.failure(error);
                return false;
            }
            giniApiDocumentRotationMap.put(document, entry.getValue());
        }
        return true;
    }

    @Nullable
    net.gini.android.models.Document getAnalyzedGiniApiDocument() {
        return mAnalyzedGiniApiDocument;
    }

    Gini getGiniApi() {
        return mGiniApi;
    }

    private String getTaskErrorMessage(@NonNull final Task task) {
        if (!task.isFaulted()) {
            return "";
        }
        final String errorMessage = task.getError().getMessage();
        return errorMessage != null ? errorMessage : task.getError().toString();
    }

    public static class Builder {

        private final Context mContext;
        private String mClientId;
        private String mClientSecret;
        private String mEmailDomain;
        private SessionManager mSessionManager;
        private String mBaseUrl;
        private String mUserCenterBaseUrl;
        private Cache mCache;
        private CredentialsStore mCredentialsStore;
        private long mConnectionTimeout;
        private TimeUnit mConnectionTimeoutUnit;
        private int mMaxNumberOfRetries;
        private float mBackoffMultiplier;

        Builder(@NonNull final Context context) {
            mContext = context;
        }

        @NonNull
        public GiniVisionDefaultNetworkService build() {
            final SdkBuilder sdkBuilder;
            if (mSessionManager != null) {
                sdkBuilder = new SdkBuilder(mContext, mSessionManager);
            } else {
                sdkBuilder = new SdkBuilder(mContext, mClientId, mClientSecret, mEmailDomain);
            }
            if (!TextUtils.isEmpty(mBaseUrl)) {
                sdkBuilder.setApiBaseUrl(mBaseUrl);
            }
            if (!TextUtils.isEmpty(mUserCenterBaseUrl)) {
                sdkBuilder.setUserCenterApiBaseUrl(mUserCenterBaseUrl);
            }
            if (mCache != null) {
                sdkBuilder.setCache(mCache);
            }
            if (mCredentialsStore != null) {
                sdkBuilder.setCredentialsStore(mCredentialsStore);
            }
            if (mConnectionTimeoutUnit != null) {
                sdkBuilder.setConnectionTimeoutInMs(
                        (int) TimeUnit.MILLISECONDS.convert(mConnectionTimeout,
                                mConnectionTimeoutUnit));
            }
            if (mMaxNumberOfRetries >= 0) {
                sdkBuilder.setMaxNumberOfRetries(mMaxNumberOfRetries);
            }
            if (mBackoffMultiplier >= 0) {
                sdkBuilder.setConnectionBackOffMultiplier(mBackoffMultiplier);
            }
            final Gini giniApi = sdkBuilder.build();
            return new GiniVisionDefaultNetworkService(giniApi);
        }

        @NonNull
        public Builder setClientCredentials(@NonNull final String clientId,
                @NonNull final String clientSecret, @NonNull final String emailDomain) {
            mClientId = clientId;
            mClientSecret = clientSecret;
            mEmailDomain = emailDomain;
            return this;
        }

        @NonNull
        public Builder setSessionManager(@NonNull final SessionManager sessionManager) {
            mSessionManager = sessionManager;
            return this;
        }

        @NonNull
        public Builder setBaseUrl(@NonNull final String baseUrl) {
            mBaseUrl = baseUrl;
            return this;
        }

        @NonNull
        public Builder setUserCenterBaseUrl(@NonNull final String userCenterBaseUrl) {
            mUserCenterBaseUrl = userCenterBaseUrl;
            return this;
        }

        @NonNull
        public Builder setCache(@NonNull final Cache cache) {
            mCache = cache;
            return this;
        }

        @NonNull
        public Builder setCredentialsStore(@NonNull final CredentialsStore credentialsStore) {
            mCredentialsStore = credentialsStore;
            return this;
        }

        @NonNull
        public Builder setConnectionTimeout(final long connectionTimeout) {
            mConnectionTimeout = connectionTimeout;
            return this;
        }

        @NonNull
        public Builder setConnectionTimeoutUnit(@NonNull final TimeUnit connectionTimeoutUnit) {
            mConnectionTimeoutUnit = connectionTimeoutUnit;
            return this;
        }

        @NonNull
        public Builder setMaxNumberOfRetries(final int maxNumberOfRetries) {
            mMaxNumberOfRetries = maxNumberOfRetries;
            return this;
        }

        @NonNull
        public Builder setBackoffMultiplier(final float backoffMultiplier) {
            mBackoffMultiplier = backoffMultiplier;
            return this;
        }
    }

}
