package net.gini.android.vision.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public interface GiniVisionNetworkApi {

    void sendFeedback(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final Callback<Void, Error> callback);

    interface Callback<R, E> {

        void failure(E error);

        void success(R result);

        void cancelled();
    }

}
