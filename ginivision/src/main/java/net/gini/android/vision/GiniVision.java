package net.gini.android.vision;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.cache.DocumentDataMemoryCache;
import net.gini.android.vision.internal.cache.PhotoMemoryCache;
import net.gini.android.vision.internal.document.ImageMultiPageDocumentMemoryStore;
import net.gini.android.vision.internal.network.NetworkRequestsManager;
import net.gini.android.vision.internal.storage.ImageDiskStore;
import net.gini.android.vision.network.GiniVisionNetworkApi;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.onboarding.OnboardingPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVision {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVision.class);
    private static GiniVision sInstance;
    private final GiniVisionNetworkService mGiniVisionNetworkService;
    private final GiniVisionNetworkApi mGiniVisionNetworkApi;
    private final NetworkRequestsManager mNetworkRequestsManager;
    private final DocumentDataMemoryCache mDocumentDataMemoryCache;
    private final PhotoMemoryCache mPhotoMemoryCache;
    private final ImageDiskStore mImageDiskStore;
    private final ImageMultiPageDocumentMemoryStore mImageMultiPageDocumentMemoryStore;
    private final Internal mInternal;
    private final DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes;
    private final boolean mFileImportEnabled;
    private final boolean mQRCodeScanningEnabled;
    private final ArrayList<OnboardingPage> mCustomOnboardingPages; // NOPMD - Bundle req. ArrayList
    private final boolean mShouldShowOnboardingAtFirstRun;
    private boolean mShouldShowOnboarding;

    @NonNull
    public static GiniVision getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Not instantiated.");
        }
        return sInstance;
    }

    public static boolean hasInstance() {
        return sInstance != null;
    }

    @NonNull
    public static Builder newInstance() {
        if (sInstance != null) {
            throw new IllegalStateException("An instance was already created. "
                    + "Call GiniVision.cleanup() before creating a new instance.");
        }
        return new Builder();
    }

    public static void cleanup(@NonNull final Context context) {
        if (sInstance != null) {
            sInstance.mDocumentDataMemoryCache.clear();
            sInstance.mPhotoMemoryCache.clear();
            if (sInstance.mNetworkRequestsManager != null) {
                sInstance.mNetworkRequestsManager.cleanup();
            }
            sInstance.mImageMultiPageDocumentMemoryStore.clear();
            sInstance = null;
        }
        ImageDiskStore.clear(context);
    }

    private GiniVision(@NonNull final Builder builder) {
        mGiniVisionNetworkService = builder.getGiniVisionNetworkService();
        mGiniVisionNetworkApi = builder.getGiniVisionNetworkApi();
        mDocumentImportEnabledFileTypes = builder.getDocumentImportEnabledFileTypes();
        mFileImportEnabled = builder.isFileImportEnabled();
        mQRCodeScanningEnabled = builder.isQRCodeScanningEnabled();
        mCustomOnboardingPages = builder.getOnboardingPages();
        mShouldShowOnboardingAtFirstRun = builder.shouldShowOnboardingAtFirstRun();
        mShouldShowOnboarding = builder.shouldShowOnboarding();
        mDocumentDataMemoryCache = new DocumentDataMemoryCache();
        mPhotoMemoryCache = new PhotoMemoryCache(mDocumentDataMemoryCache);
        mImageDiskStore = new ImageDiskStore();
        mNetworkRequestsManager = mGiniVisionNetworkService != null ? new NetworkRequestsManager(
                mGiniVisionNetworkService, mDocumentDataMemoryCache) : null;
        mImageMultiPageDocumentMemoryStore = new ImageMultiPageDocumentMemoryStore();
        mInternal = new Internal(this);
    }

    @NonNull
    ImageMultiPageDocumentMemoryStore getImageMultiPageDocumentMemoryStore() {
        return mImageMultiPageDocumentMemoryStore;
    }

    @NonNull
    public Internal internal() {
        return mInternal;
    }

    @Nullable
    public GiniVisionNetworkApi getGiniVisionNetworkApi() {
        return mGiniVisionNetworkApi;
    }

    /**
     * <p>
     *     Retrieve the file types enabled for document import.
     * </p>
     * <p>
     *     Disabled by default.
     * </p>
     * @return enabled file types
     */
    @NonNull
    public DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes() {
        return mDocumentImportEnabledFileTypes;
    }

    /**
     * <p>
     *     Find out whether file import has been enabled.
     * </p>
     * <p>
     *     Disabled by default.
     * </p>
     * @return {@code true} if file import was enabled
     */
    public boolean isFileImportEnabled() {
        return mFileImportEnabled;
    }

    /**
     * <p>
     *     Find out whether QRCode scanning has been enabled.
     * </p>
     * <p>
     *     Disabled by default.
     * </p>
     * @return {@code true} if QRCode scanning was enabled
     */
    public boolean isQRCodeScanningEnabled() {
        return mQRCodeScanningEnabled;
    }

    /**
     * The custom Onboarding Screen pages, if configured.
     *
     * @return list of {@link OnboardingPage}s
     */
    @Nullable
    public ArrayList<OnboardingPage> getCustomOnboardingPages() {
        return mCustomOnboardingPages;
    }

    /**
     *  <h3>Screen API Only</h3>
     *
     * If set to {@code false}, the Onboarding Screen won't be shown on the first run.
     *
     * @return whether to show the Onboarding Screen or not
     */
    public boolean shouldShowOnboardingAtFirstRun() {
        return mShouldShowOnboardingAtFirstRun;
    }

    /**
     * <h3>Screen API Only</h3>
     *
     * If set to {@code true}, the Onboarding Screen will be shown every every time the CameraActivity starts.
     * <p>
     * Default value is {@code false}.
     * <p>
     * You can change it on the existing GiniVision instance with {@link GiniVision#setShouldShowOnboarding(boolean)}.
     *
     * @return whether to show the Onboarding Screen or not
     */
    public boolean shouldShowOnboarding() {
        return mShouldShowOnboarding;
    }

    /**
     * <h3>Screen API Only</h3>
     *
     * Set to {@code true} to show the Onboarding Screen every time the CameraActivity starts.
     * <p>
     * Default value is {@code false}.
     *
     * @param shouldShowOnboarding whether to show the onboarding on every launch
     */
    public void setShouldShowOnboarding(final boolean shouldShowOnboarding) {
        mShouldShowOnboarding = shouldShowOnboarding;
    }

    @Nullable
    GiniVisionNetworkService getGiniVisionNetworkService() {
        return mGiniVisionNetworkService;
    }

    @Nullable
    NetworkRequestsManager getNetworkRequestsManager() {
        return mNetworkRequestsManager;
    }

    @NonNull
    DocumentDataMemoryCache getDocumentDataMemoryCache() {
        return mDocumentDataMemoryCache;
    }

    @NonNull
    PhotoMemoryCache getPhotoMemoryCache() {
        return mPhotoMemoryCache;
    }

    @NonNull
    ImageDiskStore getImageDiskStore() {
        return mImageDiskStore;
    }

    public static class Builder {

        private GiniVisionNetworkService mGiniVisionNetworkService;
        private GiniVisionNetworkApi mGiniVisionNetworkApi;
        private DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes =
                DocumentImportEnabledFileTypes.NONE;
        private boolean mFileImportEnabled;
        private boolean mQRCodeScanningEnabled;
        private ArrayList<OnboardingPage> mOnboardingPages; // NOPMD - ArrayList required (Bundle)
        private boolean mShouldShowOnboardingAtFirstRun = true;
        private boolean mShouldShowOnboarding;

        public void build() {
            checkNetworkingImplementations();
            sInstance = new GiniVision(this);
        }

        private void checkNetworkingImplementations() {
            if (mGiniVisionNetworkService == null) {
                LOG.warn("GiniVisionNetworkService instance not set. "
                        + "Relying on client to perform network calls."
                        + "You may provide a GiniVisionNetworkService instance with "
                        + "GiniVision.newInstance().setGiniVisionNetworkService()");
            }
            if (mGiniVisionNetworkApi == null) {
                LOG.warn("GiniVisionNetworkApi instance not set. "
                        + "Relying on client to perform network calls."
                        + "You may provide a GiniVisionNetworkApi instance with "
                        + "GiniVision.newInstance().setGiniVisionNetworkApi()");
            }
        }

        /**
         * <h3>Screen API Only</h3>
         *
         * Set to {@code false} to disable automatically showing the OnboardingActivity the first time the
         * CameraActivity is launched - we highly recommend letting the Gini Vision Library show the
         * OnboardingActivity at first run.
         * <p>
         * Default value is {@code true}.
         *
         * @param shouldShowOnboardingAtFirstRun whether to show the onboarding on first run or not
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setShouldShowOnboardingAtFirstRun(
                final boolean shouldShowOnboardingAtFirstRun) {
            mShouldShowOnboardingAtFirstRun = shouldShowOnboardingAtFirstRun;
            return this;
        }

        /**
         * Set custom pages to be shown in the Onboarding Screen.
         *
         * @param onboardingPages an {@link ArrayList} of {@link OnboardingPage}s
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setCustomOnboardingPages(
                @NonNull final ArrayList<OnboardingPage> onboardingPages) {
            mOnboardingPages = onboardingPages;
            return this;
        }

        /**
         * <h3>Screen API Only</h3>
         *
         * Set to {@code true} to show the Onboarding Screen every time the CameraActivity starts.
         * <p>
         * Default value is {@code false}.
         *
         * @param shouldShowOnboarding whether to show the onboarding on every launch
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setShouldShowOnboarding(final boolean shouldShowOnboarding) {
            mShouldShowOnboarding = shouldShowOnboarding;
            return this;
        }

        boolean shouldShowOnboardingAtFirstRun() {
            return mShouldShowOnboardingAtFirstRun;
        }

        @Nullable
        ArrayList<OnboardingPage> getOnboardingPages() {
            return mOnboardingPages;
        }

        @Nullable
        GiniVisionNetworkService getGiniVisionNetworkService() {
            return mGiniVisionNetworkService;
        }

        @NonNull
        public Builder setGiniVisionNetworkService(
                @NonNull final GiniVisionNetworkService giniVisionNetworkService) {
            mGiniVisionNetworkService = giniVisionNetworkService;
            return this;
        }

        @Nullable
        GiniVisionNetworkApi getGiniVisionNetworkApi() {
            return mGiniVisionNetworkApi;
        }

        @NonNull
        public Builder setGiniVisionNetworkApi(
                @NonNull final GiniVisionNetworkApi giniVisionNetworkApi) {
            mGiniVisionNetworkApi = giniVisionNetworkApi;
            return this;
        }

        @NonNull
        DocumentImportEnabledFileTypes getDocumentImportEnabledFileTypes() {
            return mDocumentImportEnabledFileTypes;
        }

        /**
         * <p>
         *     Enable and configure the document import feature or disable it by passing in
         *     {@link DocumentImportEnabledFileTypes#NONE}.
         * </p>
         * <p>
         *     Disabled by default.
         * </p>
         * @param documentImportEnabledFileTypes file types to be enabled for document import
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setDocumentImportEnabledFileTypes(
                @NonNull final DocumentImportEnabledFileTypes documentImportEnabledFileTypes) {
            mDocumentImportEnabledFileTypes = documentImportEnabledFileTypes;
            return this;
        }

        boolean isFileImportEnabled() {
            return mFileImportEnabled;
        }

        /**
         * <p>
         *     Enable/disable the file import feature.
         * </p>
         * <p>
         *     Disabled by default.
         * </p>
         * @param fileImportEnabled {@code true} to enable file import
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setFileImportEnabled(final boolean fileImportEnabled) {
            mFileImportEnabled = fileImportEnabled;
            return this;
        }

        boolean isQRCodeScanningEnabled() {
            return mQRCodeScanningEnabled;
        }

        /**
         * <p>
         *     Enable/disable the QRCode scanning feature.
         * </p>
         * <p>
         *     Disabled by default.
         * </p>
         * @param qrCodeScanningEnabled {@code true} to enable QRCode scanning
         * @return the {@link Builder} instance
         */
        @NonNull
        public Builder setQRCodeScanningEnabled(final boolean qrCodeScanningEnabled) {
            mQRCodeScanningEnabled = qrCodeScanningEnabled;
            return this;
        }

        boolean shouldShowOnboarding() {
            return mShouldShowOnboarding;
        }
    }

    public static class Internal {

        private final GiniVision mGiniVision;

        public Internal(@NonNull final GiniVision giniVision) {
            mGiniVision = giniVision;
        }

        @Nullable
        public GiniVisionNetworkService getGiniVisionNetworkService() {
            return mGiniVision.getGiniVisionNetworkService();
        }

        @Nullable
        public NetworkRequestsManager getNetworkRequestsManager() {
            return mGiniVision.getNetworkRequestsManager();
        }

        @NonNull
        public DocumentDataMemoryCache getDocumentDataMemoryCache() {
            return mGiniVision.getDocumentDataMemoryCache();
        }

        @NonNull
        public PhotoMemoryCache getPhotoMemoryCache() {
            return mGiniVision.getPhotoMemoryCache();
        }

        public ImageDiskStore getImageDiskStore() {
            return mGiniVision.getImageDiskStore();
        }

        public ImageMultiPageDocumentMemoryStore getImageMultiPageDocumentMemoryStore() {
            return mGiniVision.getImageMultiPageDocumentMemoryStore();
        }
    }

}
