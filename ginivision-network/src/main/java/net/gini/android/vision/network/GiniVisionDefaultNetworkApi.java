package net.gini.android.vision.network;

import net.gini.android.DocumentTaskManager;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.network.model.SpecificExtractionMapper;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import androidx.annotation.NonNull;
import bolts.Continuation;
import bolts.Task;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Default implementation of network calls which can be performed manually from outside the Gini
 * Vision Library (e.g. for sending feedback).
 *
 * <p> To create an instance use the {@link GiniVisionDefaultNetworkApi.Builder} returned by the
 * {@link #builder()} method.
 *
 * <p> In order to easily access this implementation pass an instance of it to {@link
 * GiniVision.Builder#setGiniVisionNetworkApi(GiniVisionNetworkApi)} when creating a {@link
 * GiniVision} instance. You can then get the instance in your app with {@link
 * GiniVision#getGiniVisionNetworkApi()}.
 */
public class GiniVisionDefaultNetworkApi implements GiniVisionNetworkApi {

    private static final Logger LOG = LoggerFactory.getLogger(GiniVisionDefaultNetworkApi.class);

    private final GiniVisionDefaultNetworkService mDefaultNetworkService;
    private final UIExecutor mUIExecutor = new UIExecutor();

    /**
     * Creates a new {@link GiniVisionDefaultNetworkApi.Builder} to configure and create a new instance.
     *
     * @return a new {@link GiniVisionDefaultNetworkApi.Builder}
     */
    public static Builder builder() {
        return new Builder();
    }

    GiniVisionDefaultNetworkApi(
            @NonNull final GiniVisionDefaultNetworkService defaultNetworkService) {
        mDefaultNetworkService = defaultNetworkService;
    }

    @Override
    public void sendFeedback(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final GiniVisionNetworkCallback<Void, Error> callback) {
        final DocumentTaskManager documentTaskManager = mDefaultNetworkService.getGiniApi()
                .getDocumentTaskManager();
        final net.gini.android.models.Document document =
                mDefaultNetworkService.getAnalyzedGiniApiDocument();
        // We require the Gini API SDK's net.gini.android.models.Document for sending the feedback
        if (document != null) {
            LOG.debug("Send feedback for api document {} using extractions {}", document.getId(),
                    extractions);
            try {
                documentTaskManager.sendFeedbackForExtractions(document,
                        SpecificExtractionMapper.mapToApiSdk(extractions))
                        .continueWith(new Continuation<net.gini.android.models.Document, Object>() {
                            @Override
                            public Object then(
                                    @NonNull final Task<net.gini.android.models.Document> task) {
                                mUIExecutor.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (task.isFaulted()) {
                                            LOG.error(
                                                    "Send feedback failed for api document {}: {}",
                                                    document.getId(), task.getError());
                                            String message = "unknown";
                                            if (task.getError() != null) {
                                                message = task.getError().getMessage();
                                            }
                                            callback.failure(new Error(message));
                                        } else {
                                            LOG.debug("Send feedback success for api document {}",
                                                    document.getId());
                                            callback.success(null);
                                        }
                                    }
                                });
                                return null;
                            }
                        });
            } catch (final JSONException e) {
                LOG.error("Send feedback failed for api document {}: {}", document.getId(), e);
                callback.failure(new Error(e.getMessage()));
            }
        } else {
            LOG.error("Send feedback failed: no Gini Api Document available");
            callback.failure(new Error("Feedback not set: no Gini Api Document available"));
        }
    }

    @Override
    public void deleteGiniUserCredentials() {
        mDefaultNetworkService.getGiniApi().getCredentialsStore().deleteUserCredentials();
    }

    /**
     * Builder for configuring a new instance of the {@link GiniVisionDefaultNetworkApi}.
     */
    public static class Builder {

        private GiniVisionDefaultNetworkService mDefaultNetworkService;

        Builder() {
        }

        /**
         * Set the same {@link GiniVisionDefaultNetworkService} instance you use for {@link
         * GiniVision}.
         *
         * @param networkService {@link GiniVisionDefaultNetworkService} instance
         *
         * @return the {@link Builder} instance
         */
        public Builder withGiniVisionDefaultNetworkService(
                @NonNull final GiniVisionDefaultNetworkService networkService) {
            mDefaultNetworkService = networkService;
            return this;
        }

        /**
         * Create a new instance of the {@link GiniVisionDefaultNetworkApi}.
         *
         * @return new {@link GiniVisionDefaultNetworkApi} instance
         */
        public GiniVisionDefaultNetworkApi build() {
            if (mDefaultNetworkService == null) {
                throw new IllegalStateException(
                        "GiniVisionDefaultNetworkApi requires a GiniVisionDefaultNetworkService instance.");
            }
            return new GiniVisionDefaultNetworkApi(mDefaultNetworkService);
        }
    }
}
