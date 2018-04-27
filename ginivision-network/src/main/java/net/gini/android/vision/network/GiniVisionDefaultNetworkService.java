package net.gini.android.vision.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Cache;

import net.gini.android.Gini;
import net.gini.android.SdkBuilder;
import net.gini.android.authorization.CredentialsStore;
import net.gini.android.authorization.SessionManager;
import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.network.model.SpecificExtractionMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionDefaultNetworkService implements GiniVisionNetworkService {

    private final Gini mGiniApi;
    private final Map<String, net.gini.android.models.Document> mApiDocuments = new HashMap<>();

    public static Builder builder(@NonNull final Context context) {
        return new Builder(context);
    }

    GiniVisionDefaultNetworkService(@NonNull final Gini giniApi) {
        mGiniApi = giniApi;
    }

    Gini getGiniApi() {
        return mGiniApi;
    }

    @Override
    public CancellationToken upload(@NonNull final Document document,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        if (document.getData() == null) {
            callback.failure(new Error("Document has no data. Did you forget to load it?"));
            return new CancellationToken() {
                @Override
                public void cancel() {
                }
            };
        }
        final String contentType;
        switch (document.getType()) {
            case IMAGE:
                // WIP-MPA: add image file type to document
                contentType = MimeType.IMAGE_JPEG.asString();
                break;
            case PDF:
                contentType = MimeType.APPLICATION_PDF.asString();
                break;
            case QRCode:
                // WIP-MPA: create and use a content type enum
                contentType = "application/json";
                break;
            default:
                throw new IllegalArgumentException(
                        "Unsupported document type " + document.getType());
        }
        mGiniApi.getDocumentTaskManager()
                .createPartialDocument(document.getData(), contentType, null, null)
                .continueWith(new Continuation<net.gini.android.models.Document, Void>() {
                    @Override
                    public Void then(final Task<net.gini.android.models.Document> task)
                            throws Exception {
                        if (task.isFaulted()) {
                            callback.failure(new Error(task.getError().getMessage()));
                        } else if (task.getResult() != null) {
                            final net.gini.android.models.Document apiDocument = task.getResult();
                            mApiDocuments.put(apiDocument.getId(), apiDocument);
                            callback.success(new Result(apiDocument.getId()));
                        } else {
                            // WIP-MPA: call cancelled only here after API SDK supports cancellation
                            callback.cancelled();
                        }
                        return null;
                    }
                });
        return new CancellationToken() {
            @Override
            public void cancel() {
                // WIP-MPA: how to cancel a task in the API SDK?
                // WIP-MPA: don't call cancelled here after API SDK supports cancellation
                callback.cancelled();
            }
        };
    }

    @Override
    public CancellationToken delete(@NonNull final String documentId,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        mGiniApi.getDocumentTaskManager().deletePartialDocumentAndParents(documentId)
                .continueWith(new Continuation<String, Void>() {
                    @Override
                    public Void then(final Task<String> task) throws Exception {
                        if (task.isFaulted()) {
                            callback.failure(new Error(task.getError().getMessage()));
                        } else if (task.getResult() != null) {
                            callback.success(new Result(documentId));
                        } else {
                            // WIP-MPA: call cancelled only here after API SDK supports cancellation
                            callback.cancelled();
                        }
                        return null;
                    }
                });
        return new CancellationToken() {
            @Override
            public void cancel() {
                // WIP-MPA: how to cancel a task in the API SDK?
                // WIP-MPA: don't call cancelled here after API SDK supports cancellation
                callback.cancelled();
            }
        };
    }

    @Override
    public CancellationToken analyze(
            @NonNull final LinkedHashMap<String, Integer> documentIdRotationMap,
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
        final LinkedHashMap<net.gini.android.models.Document, Integer> documentRotationMap =
                new LinkedHashMap<>();
        for (final Map.Entry<String, Integer> entry : documentIdRotationMap.entrySet()) {
            final net.gini.android.models.Document document = mApiDocuments.get(entry.getKey());
            if (document == null) {
                callback.failure(new Error("Missing partial document."));
                return new CancellationToken() {
                    @Override
                    public void cancel() {
                    }
                };
            }
            documentRotationMap.put(document, entry.getValue());
        }
        final AtomicReference<net.gini.android.models.Document> compositeDocument =
                new AtomicReference<>();
        mGiniApi.getDocumentTaskManager().createCompositeDocument(documentRotationMap, null)
                .onSuccessTask(
                        new Continuation<net.gini.android.models.Document, Task<net.gini.android.models.Document>>() {
                            @Override
                            public Task<net.gini.android.models.Document> then(
                                    final Task<net.gini.android.models.Document> task)
                                    throws Exception {
                                final net.gini.android.models.Document giniDocument =
                                        task.getResult();
                                if (task.isCancelled()) {
                                    return task;
                                }
                                final net.gini.android.models.Document apiDocument =
                                        task.getResult();
                                compositeDocument.set(apiDocument);
                                mApiDocuments.put(apiDocument.getId(), apiDocument);
                                return mGiniApi.getDocumentTaskManager().pollDocument(giniDocument);
                            }
                        })
                .onSuccessTask(
                        new Continuation<net.gini.android.models.Document, Task<Map<String, SpecificExtraction>>>() {
                            @Override
                            public Task<Map<String, SpecificExtraction>> then(
                                    final Task<net.gini.android.models.Document> task)
                                    throws Exception {
                                final net.gini.android.models.Document giniDocument =
                                        task.getResult();
                                if (task.isCancelled()) {
                                    return Task.cancelled();
                                }
                                return mGiniApi.getDocumentTaskManager().getExtractions(
                                        giniDocument);
                            }
                        })
                .continueWith(
                        new Continuation<Map<String, SpecificExtraction>, Void>() {
                            @Override
                            public Void then(
                                    final Task<Map<String, SpecificExtraction>> task)
                                    throws Exception {
                                if (task.isFaulted()) {
                                    callback.failure(new Error(task.getError().getMessage()));
                                } else if (task.getResult() != null) {
                                    final Map<String, GiniVisionSpecificExtraction> extractions =
                                            SpecificExtractionMapper.mapToGVL(task.getResult());
                                    callback.success(
                                            new AnalysisResult(compositeDocument.get().getId(),
                                                    extractions));
                                } else {
                                    // WIP-MPA: call cancelled only here after API SDK supports cancellation
                                    callback.cancelled();
                                }
                                return null;
                            }
                        });
        return new CancellationToken() {
            @Override
            public void cancel() {
                // WIP-MPA: how to cancel a task in the API SDK?
                // WIP-MPA: don't call cancelled here after API SDK supports cancellation
                callback.cancelled();
            }
        };

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
