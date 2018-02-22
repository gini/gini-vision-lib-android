package net.gini.android.vision;

import android.support.annotation.NonNull;

import net.gini.android.vision.network.GiniVisionNetworkApi;
import net.gini.android.vision.network.GiniVisionNetworkService;

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

    public static GiniVision getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException("Not instantiated.");
        }
        return sInstance;
    }

    public static boolean hasInstance() {
        return sInstance != null;
    }

    public static Builder newInstance() {
        return new Builder();
    }

    public static void cleanup() {
        sInstance = null;
    }

    private GiniVision(@NonNull final Builder builder) {
        mGiniVisionNetworkService = builder.getGiniVisionNetworkService();
        mGiniVisionNetworkApi = builder.getGiniVisionNetworkApi();
        mInternal = new Internal(this);
    }

    public Internal internal() {
        return mInternal;
    }

    public GiniVisionNetworkApi getGiniVisionNetworkApi() {
        return mGiniVisionNetworkApi;
    }

    GiniVisionNetworkService getGiniVisionNetworkService() {
        return mGiniVisionNetworkService;
    }

    public static class Builder {

        private GiniVisionNetworkService mGiniVisionNetworkService;
        private GiniVisionNetworkApi mGiniVisionNetworkApi;

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

        GiniVisionNetworkService getGiniVisionNetworkService() {
            return mGiniVisionNetworkService;
        }

        public Builder setGiniVisionNetworkService(
                final GiniVisionNetworkService giniVisionNetworkService) {
            mGiniVisionNetworkService = giniVisionNetworkService;
            return this;
        }

        GiniVisionNetworkApi getGiniVisionNetworkApi() {
            return mGiniVisionNetworkApi;
        }

        public Builder setGiniVisionNetworkApi(
                final GiniVisionNetworkApi giniVisionNetworkApi) {
            mGiniVisionNetworkApi = giniVisionNetworkApi;
            return this;
        }
    }

    public static class Internal {

        private final GiniVision mGiniVision;

        public Internal(final GiniVision giniVision) {
            mGiniVision = giniVision;
        }

        public GiniVisionNetworkService getGiniVisionNetworkService() {
            return mGiniVision.getGiniVisionNetworkService();
        }
    }

}
