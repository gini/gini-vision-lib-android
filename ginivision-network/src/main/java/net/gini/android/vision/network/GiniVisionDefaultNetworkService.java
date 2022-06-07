package net.gini.android.vision.network;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.Cache;

import net.gini.android.DocumentMetadata;
import net.gini.android.DocumentTaskManager;
import net.gini.android.Gini;
import net.gini.android.SdkBuilder;
import net.gini.android.authorization.CredentialsStore;
import net.gini.android.authorization.SessionManager;
import net.gini.android.authorization.SharedPreferencesCredentialsStore;
import net.gini.android.models.ExtractionsContainer;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.network.model.SpecificExtractionMapper;
import net.gini.android.vision.util.CancellationToken;
import net.gini.android.vision.util.NoOpCancellationToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.XmlRes;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Default implementation of the network related tasks required by the Gini Vision Library.
 *
 * <p> Relies on the <a href="http://developer.gini.net/gini-sdk-android/">Gini API SDK</a> for
 * executing the requests, which implements communication with the Gini API using generated
 * anonymous Gini users.
 *
 * <p><b>Important:</b> Access to the Gini User Center API is required which is restricted to
 * selected clients only. Contact Gini if you require access.
 *
 * <p> To create an instance use the {@link GiniVisionDefaultNetworkService.Builder} returned by the
 * {@link #builder(Context)} method.
 *
 * <p> In order for the Gini Vision Library to use this implementation pass an instance of it to
 * {@link GiniVision.Builder#setGiniVisionNetworkService(GiniVisionNetworkService)} when creating a
 * {@link GiniVision} instance.
 */
public class GiniVisionDefaultNetworkService implements GiniVisionNetworkService {

    private static final Logger LOG = LoggerFactory.getLogger(
            GiniVisionDefaultNetworkService.class);

    private final Gini mGiniApi;
    private final Map<String, net.gini.android.models.Document> mGiniApiDocuments = new HashMap<>();
    private final DocumentMetadata mDocumentMetadata;
    private net.gini.android.models.Document mAnalyzedGiniApiDocument;

    /**
     * Creates a new {@link GiniVisionDefaultNetworkService.Builder} to configure and create a new
     * instance.
     *
     * @param context Android context
     *
     * @return a new {@link GiniVisionDefaultNetworkService.Builder}
     */
    public static Builder builder(@NonNull final Context context) {
        return new Builder(context);
    }

    GiniVisionDefaultNetworkService(@NonNull final Gini giniApi,
            @Nullable final DocumentMetadata documentMetadata) {
        mGiniApi = giniApi;
        mDocumentMetadata = documentMetadata;
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
        final DocumentTaskManager documentTaskManager = mGiniApi.getDocumentTaskManager();
        final Task<net.gini.android.models.Document> createDocumentTask;
        if (mDocumentMetadata != null) {
            createDocumentTask = documentTaskManager.createPartialDocument(document.getData(),
                    document.getMimeType(), null, null, mDocumentMetadata);
        } else {
            createDocumentTask = documentTaskManager.createPartialDocument(document.getData(),
                    document.getMimeType(), null, null);
        }
        createDocumentTask.continueWith(new Continuation<net.gini.android.models.Document, Void>() {
                    @Override
                    public Void then(final Task<net.gini.android.models.Document> task)
                            throws Exception {
                        if (task.isFaulted()) {
                            final Error error = new Error(getTaskErrorMessage(task), task.getError());
                            LOG.error("Document upload failed for {}: {}", document.getId(),
                                    error.getMessage());
                            callback.failure(error);
                        } else if (task.getResult() != null) {
                            final net.gini.android.models.Document apiDocument = task.getResult();
                            LOG.debug("Document upload success for {}: {}", document.getId(),
                                    apiDocument);
                            mGiniApiDocuments.put(apiDocument.getId(), apiDocument);
                            callback.success(new Result(apiDocument.getId()));
                        } else {
                            LOG.debug("Document upload cancelled for {}", document.getId());
                            callback.cancelled();
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
        return new NoOpCancellationToken();
    }

    private String getTaskErrorMessage(@NonNull final Task task) {
        if (!task.isFaulted()) {
            return "";
        }
        final String errorMessage = task.getError().getMessage();
        return errorMessage != null ? errorMessage : task.getError().toString();
    }


    @Override
    public CancellationToken delete(@NonNull final String giniApiDocumentId,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {
        LOG.debug("Delete document with api id {}", giniApiDocumentId);
        mGiniApi.getDocumentTaskManager().deletePartialDocumentAndParents(giniApiDocumentId)
                .continueWith(new Continuation<String, Void>() {
                    @Override
                    public Void then(final Task<String> task) throws Exception {
                        if (task.isFaulted()) {
                            final Error error = new Error(getTaskErrorMessage(task), task.getError());
                            LOG.error("Document deletion failed for api id {}: {}",
                                    giniApiDocumentId,
                                    error.getMessage());
                            callback.failure(error);
                        } else if (task.getResult() != null) {
                            LOG.debug("Document deletion success for api id {}", giniApiDocumentId);
                            callback.success(new Result(giniApiDocumentId));
                        } else {
                            LOG.debug("Document deletion cancelled for api id {}",
                                    giniApiDocumentId);
                            callback.cancelled();
                        }
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
        return new NoOpCancellationToken();
    }

    @SuppressWarnings("PMD.ExcessiveMethodLength")
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
        mAnalyzedGiniApiDocument = null; // NOPMD
        final AtomicBoolean isCancelled = new AtomicBoolean();
        final AtomicReference<net.gini.android.models.Document> compositeDocument =
                new AtomicReference<>();
        mGiniApi.getDocumentTaskManager().createCompositeDocument(giniApiDocumentRotationMap, null)
                .onSuccessTask(
                        new Continuation<net.gini.android.models.Document,
                                Task<net.gini.android.models.Document>>() {
                            @Override
                            public Task<net.gini.android.models.Document> then(
                                    final Task<net.gini.android.models.Document> task)
                                    throws Exception {
                                if (isCancelled.get()) {
                                    LOG.debug(
                                            "Document analysis cancelled after composite document creation for documents {}",
                                            giniApiDocumentIdRotationMap);
                                    return Task.cancelled();
                                }
                                if (task.isCancelled()) {
                                    LOG.debug(
                                            "Composite document creation cancelled for documents {}",
                                            giniApiDocumentIdRotationMap);
                                    return task;
                                }
                                final net.gini.android.models.Document giniApiDocument =
                                        task.getResult();
                                // Composite document needed to create the AnalysisResult later
                                compositeDocument.set(giniApiDocument);
                                mGiniApiDocuments.put(giniApiDocument.getId(), giniApiDocument);
                                return mGiniApi.getDocumentTaskManager().pollDocument(
                                        giniApiDocument);
                            }
                        })
                .onSuccessTask(
                        new Continuation<net.gini.android.models.Document,
                                Task<ExtractionsContainer>>() {
                            @Override
                            public Task<ExtractionsContainer> then(
                                    final Task<net.gini.android.models.Document> task)
                                    throws Exception {
                                if (isCancelled.get()) {
                                    LOG.debug(
                                            "Document analysis cancelled after polling for documents {}",
                                            giniApiDocumentIdRotationMap);
                                    return Task.cancelled();
                                }
                                final net.gini.android.models.Document giniApiDocument =
                                        task.getResult();
                                if (task.isCancelled()) {
                                    LOG.debug(
                                            "Composite document polling cancelled for documents {}",
                                            giniApiDocumentIdRotationMap);
                                    return Task.cancelled();
                                }
                                return mGiniApi.getDocumentTaskManager().getAllExtractions(
                                        giniApiDocument);
                            }
                        })
                .continueWith(
                        new Continuation<ExtractionsContainer, Void>() {
                            @Override
                            public Void then(
                                    final Task<ExtractionsContainer> task)
                                    throws Exception {
                                if (task.isFaulted()) {
                                    final Error error = new Error(getTaskErrorMessage(task), task.getError());
                                    LOG.error("Document analysis failed for documents {}: {}",
                                            giniApiDocumentIdRotationMap, error.getMessage());
                                    callback.failure(error);
                                } else if (task.getResult() != null) {
                                    mAnalyzedGiniApiDocument = compositeDocument.get();
                                    final Map<String, GiniVisionSpecificExtraction> extractions =
                                            SpecificExtractionMapper.mapToGVL(task.getResult().getSpecificExtractions());
                                    LOG.debug("Document analysis success for documents {}: extractions = {}; compoundExtractions = {}; returnReasons = {}",
                                            giniApiDocumentIdRotationMap, extractions);
                                    callback.success(
                                            new AnalysisResult(compositeDocument.get().getId(),
                                                    extractions));
                                } else {
                                    LOG.debug("Document analysis cancelled for documents {}",
                                            giniApiDocumentIdRotationMap);
                                    callback.cancelled();
                                }
                                return null;
                            }
                        }, Task.UI_THREAD_EXECUTOR);
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
        mAnalyzedGiniApiDocument = null; // NOPMD
        mGiniApiDocuments.clear();
    }

    private boolean collectGiniApiDocuments(
            @NonNull final LinkedHashMap<net.gini.android.models.Document, Integer> // NOPMD
                    giniApiDocumentRotationMap,
            @NonNull final LinkedHashMap<String, Integer> giniApiDocumentIdRotationMap, // NOPMD
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
        for (final Map.Entry<String, Integer> entry : giniApiDocumentIdRotationMap.entrySet()) {
            final net.gini.android.models.Document document = mGiniApiDocuments.get(entry.getKey());
            if (document == null) {
                final Error error = new Error("Missing partial document."); // NOPMD
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

    /**
     * Builder for configuring a new instance of the {@link GiniVisionDefaultNetworkService}.
     */
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
        @XmlRes
        private int mNetworkSecurityConfigResId;
        private long mConnectionTimeout;
        private TimeUnit mConnectionTimeoutUnit;
        private int mMaxNumberOfRetries;
        private float mBackoffMultiplier;
        private DocumentMetadata mDocumentMetadata;

        Builder(@NonNull final Context context) {
            mContext = context;
        }

        /**
         * Create a new instance of the {@link GiniVisionDefaultNetworkService}.
         *
         * @return new {@link GiniVisionDefaultNetworkService} instance
         */
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
            if (mNetworkSecurityConfigResId != 0) {
                sdkBuilder.setNetworkSecurityConfigResId(mNetworkSecurityConfigResId);
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
            return new GiniVisionDefaultNetworkService(giniApi, mDocumentMetadata);
        }

        /**
         * Set your Gini API client ID and secret. The email domain is used when generating
         * anonymous Gini users in the form of {@code UUID@your-email-domain}.
         *
         * @param clientId     your application's client ID for the Gini API
         * @param clientSecret your application's client secret for the Gini API
         * @param emailDomain  the email domain which is used for created Gini users
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setClientCredentials(@NonNull final String clientId,
                @NonNull final String clientSecret, @NonNull final String emailDomain) {
            mClientId = clientId;
            mClientSecret = clientSecret;
            mEmailDomain = emailDomain;
            return this;
        }

        /**
         * Set a custom {@link SessionManager} implementation for handling sessions.
         *
         * @param sessionManager the {@link SessionManager} to use
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setSessionManager(@NonNull final SessionManager sessionManager) {
            mSessionManager = sessionManager;
            return this;
        }

        /**
         * Set the base URL of the Gini API.
         *
         * @param baseUrl custom Gini API base URL
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setBaseUrl(@NonNull final String baseUrl) {
            mBaseUrl = baseUrl;
            return this;
        }

        /**
         * Set the base URL of the Gini User Center API.
         *
         * @param userCenterBaseUrl custom Gini API base URL
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setUserCenterBaseUrl(@NonNull final String userCenterBaseUrl) {
            mUserCenterBaseUrl = userCenterBaseUrl;
            return this;
        }

        /**
         * Set the cache implementation to use with Volley. If no cache is set, the default Volley
         * cache will be used.
         *
         * @param cache a cache instance (specified by the com.android.volley.Cache interface)
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setCache(@NonNull final Cache cache) {
            mCache = cache;
            return this;
        }

        /**
         * Set the credentials store which is used by the Gini API SDK to store user credentials. If
         * no credentials store is set, the {@link SharedPreferencesCredentialsStore} from the Gini
         * API SDK is used by default.
         *
         * @param credentialsStore a credentials store instance (specified by the CredentialsStore
         *                         interface)
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setCredentialsStore(@NonNull final CredentialsStore credentialsStore) {
            mCredentialsStore = credentialsStore;
            return this;
        }

        /**
         * Set the resource id for the network security configuration xml to enable public key pinning.
         *
         * @param networkSecurityConfigResId xml resource id
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setNetworkSecurityConfigResId(@XmlRes final int networkSecurityConfigResId) {
            mNetworkSecurityConfigResId = networkSecurityConfigResId;
            return this;
        }

        /**
         * Set the (initial) timeout for each request. A timeout error will occur if nothing is
         * received from the underlying socket in the given time span. The initial timeout will be
         * altered depending on the backoff multiplier and failed retries.
         *
         * @param connectionTimeout initial timeout
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setConnectionTimeout(final long connectionTimeout) {
            mConnectionTimeout = connectionTimeout;
            return this;
        }

        /**
         * Set the connection timeout's time unit.
         *
         * @param connectionTimeoutUnit the time unit
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setConnectionTimeoutUnit(@NonNull final TimeUnit connectionTimeoutUnit) {
            mConnectionTimeoutUnit = connectionTimeoutUnit;
            return this;
        }

        /**
         * Set the maximal number of retries for each network request.
         *
         * @param maxNumberOfRetries maximal number of retries
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setMaxNumberOfRetries(final int maxNumberOfRetries) {
            mMaxNumberOfRetries = maxNumberOfRetries;
            return this;
        }

        /**
         * Sets the backoff multiplication factor for connection retries. In case of failed retries
         * the timeout of the last request attempt is multiplied by this factor.
         *
         * @param backoffMultiplier the backoff multiplication factor
         *
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setBackoffMultiplier(final float backoffMultiplier) {
            mBackoffMultiplier = backoffMultiplier;
            return this;
        }

        /**
         * Set additional information related to the documents. This metadata will be passed to all
         * document uploads.
         *
         * @param documentMetadata a {@link DocumentMetadata} instance containing additional
         *                         information for the uploaded documents
         * @return the {@link Builder} instance
         */
        public Builder setDocumentMetadata(@NonNull final DocumentMetadata documentMetadata) {
            mDocumentMetadata = documentMetadata;
            return this;
        }

    }

}
