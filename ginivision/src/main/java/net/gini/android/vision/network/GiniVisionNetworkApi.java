package net.gini.android.vision.network;

import net.gini.android.vision.GiniVision;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Interface specifying network calls which can be performed manually from outside the Gini Vision
 * Library (e.g. for sending feedback).
 *
 * * <p> In order to easily access your implementation pass an instance of it to {@link
 * GiniVision.Builder#setGiniVisionNetworkApi(GiniVisionNetworkApi)} when creating a {@link
 * GiniVision} instance. You can then get the instance in your app with {@link
 * GiniVision#getGiniVisionNetworkApi()}.
 */
public interface GiniVisionNetworkApi {

    /**
     * Call this method with the extractions the user has seen and accepted. The {@link
     * GiniVisionSpecificExtraction}s must contain the final user corrected and/or accepted values.
     *
     * @param extractions a map of extraction labels and specific extractions
     * @param callback    a callback implementation to return the outcome
     */
    void sendFeedback(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final GiniVisionNetworkCallback<Void, Error> callback);

    /**
     * Delete the anonymous gini user credentials. These were automatically generated when the first document was uploaded.
     * <p>
     * By deleting the credentials, new ones will be generated at the next upload.
     */
    void deleteGiniUserCredentials();
}
