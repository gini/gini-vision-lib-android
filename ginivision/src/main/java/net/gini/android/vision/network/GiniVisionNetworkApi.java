package net.gini.android.vision.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Interface specifying network calls which can be performed manually from outside the Gini Vision
 * Library (e.g. for sending feedback).
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
}
