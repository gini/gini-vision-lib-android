package net.gini.android.vision;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.network.GiniVisionNetworkApi;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.onboarding.OnboardingPage;

import java.util.ArrayList;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class GiniVision {

    private static GiniVision sInstance;
    private final GiniVisionNetworkService mGiniVisionNetworkService;
    private final GiniVisionNetworkApi mGiniVisionNetworkApi;
    private final Internal mInternal;
    private final DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes;
    private final boolean mFileImportEnabled;
    private final boolean mQRCodeScanningEnabled;
    private final ArrayList<OnboardingPage> mOnboardingPages; // NOPMD - ArrayList required (Bundle)

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

    public static void cleanup() {
        sInstance = null;
    }

    private GiniVision(@NonNull final Builder builder) {
        mGiniVisionNetworkService = builder.getGiniVisionNetworkService();
        mGiniVisionNetworkApi = builder.getGiniVisionNetworkApi();
        mDocumentImportEnabledFileTypes = builder.getDocumentImportEnabledFileTypes();
        mFileImportEnabled = builder.isFileImportEnabled();
        mQRCodeScanningEnabled = builder.isQRCodeScanningEnabled();
        mOnboardingPages = builder.getOnboardingPages();
        mInternal = new Internal(this);
    }

    @NonNull
    public Internal internal() {
        return mInternal;
    }

    @NonNull
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

    @Nullable
    public ArrayList<OnboardingPage> getOnboardingPages() {
        return mOnboardingPages;
    }

    @NonNull
    GiniVisionNetworkService getGiniVisionNetworkService() {
        return mGiniVisionNetworkService;
    }

    public static class Builder {

        private GiniVisionNetworkService mGiniVisionNetworkService;
        private GiniVisionNetworkApi mGiniVisionNetworkApi;
        private DocumentImportEnabledFileTypes mDocumentImportEnabledFileTypes =
                DocumentImportEnabledFileTypes.NONE;
        private boolean mFileImportEnabled;
        private boolean mQRCodeScanningEnabled;
        private ArrayList<OnboardingPage> mOnboardingPages; // NOPMD - ArrayList required (Bundle)

        public void build() {
            checkRequiredFields();
            sInstance = new GiniVision(this);
        }

        private void checkRequiredFields() {
            if (mGiniVisionNetworkService == null) {
                throw new IllegalStateException("A GiniVisionNetworkService instance is required"
                        + " for creating the GiniVision instance. Please provide one with "
                        + "GiniVision.newInstance().setGiniVisionNetworkService()");
            }
            if (mGiniVisionNetworkApi == null) {
                throw new IllegalStateException("A GiniVisionNetworkApi instance is required "
                        + "for creating the GiniVision instance. Please provide one with "
                        + "GiniVision.newInstance().setGiniVisionNetworkApi()");
            }
        }

        @Nullable
        ArrayList<OnboardingPage> getOnboardingPages() {
            return mOnboardingPages;
        }

        @NonNull
        public Builder setOnboardingPages(@NonNull final ArrayList<OnboardingPage> onboardingPages) {
            mOnboardingPages = onboardingPages;
            return this;
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
    }

    public static class Internal {

        private final GiniVision mGiniVision;

        public Internal(@NonNull final GiniVision giniVision) {
            mGiniVision = giniVision;
        }

        @NonNull
        public GiniVisionNetworkService getGiniVisionNetworkService() {
            return mGiniVision.getGiniVisionNetworkService();
        }
    }

}
