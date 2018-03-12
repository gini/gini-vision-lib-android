package net.gini.android.vision.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.android.volley.Cache;

import net.gini.android.Gini;
import net.gini.android.SdkBuilder;
import net.gini.android.authorization.CredentialsStore;
import net.gini.android.authorization.SessionManager;
import net.gini.android.vision.Document;
import net.gini.android.vision.network.model.SpecificExtractionMapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alpar Szotyori on 30.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVisionDefaultNetworkService implements GiniVisionNetworkService {

    private final SingleDocumentAnalyzer mSingleDocumentAnalyzer;
    private final Gini mGiniApi;

    public static Builder builder(@NonNull final Context context) {
        return new Builder(context);
    }

    GiniVisionDefaultNetworkService(@NonNull final Gini giniApi,
            @NonNull final SingleDocumentAnalyzer singleDocumentAnalyzer) {
        mGiniApi = giniApi;
        mSingleDocumentAnalyzer = singleDocumentAnalyzer;
    }

    SingleDocumentAnalyzer getSingleDocumentAnalyzer() {
        return mSingleDocumentAnalyzer;
    }

    Gini getGiniApi() {
        return mGiniApi;
    }

    @Override
    public void analyze(@NonNull final Document document,
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback) {
        mSingleDocumentAnalyzer.analyzeDocument(document,
                new DocumentAnalyzer.Listener() {
                    @Override
                    public void onException(final Exception exception) {
                        callback.failure(new Error(exception.getMessage()));
                    }

                    @Override
                    public void onExtractionsReceived(
                            final Map<String, net.gini.android.models.SpecificExtraction> extractions) {
                        callback.success(new AnalysisResult(
                                mSingleDocumentAnalyzer.getGiniApiDocument().getId(),
                                SpecificExtractionMapper.mapToGVL(extractions)));
                    }
                });
    }

    @Override
    public void upload(@NonNull final Document document,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback) {

    }

    @Override
    public void cancel() {
        mSingleDocumentAnalyzer.cancelAnalysis();
    }

    public static class Builder {

        private final Context mContext;
        private String mClientId;
        private String mClientSecret;
        private String mEmailDomain;
        private String[] mCertificateAssetPaths;
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
            if (mCertificateAssetPaths != null) {
                sdkBuilder = new SdkBuilder(mContext, mClientId, mClientSecret,
                        mEmailDomain, mCertificateAssetPaths);
            } else if (mSessionManager != null) {
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
            final SingleDocumentAnalyzer singleDocumentAnalyzer = new SingleDocumentAnalyzer(
                    giniApi);
            return new GiniVisionDefaultNetworkService(giniApi, singleDocumentAnalyzer);
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
        public Builder setCertificateAssetPaths(@NonNull final String[] certificateAssetPaths) {
            mCertificateAssetPaths = certificateAssetPaths;
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
