package net.gini.android.vision.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public interface GiniVisionNetworkService {

    void analyze(@NonNull final Document document,
            @NonNull final Callback<AnalysisResult, Error> callback);

    void upload(@NonNull final Document document,
            @NonNull final Callback<Result, Error> callback);

    void sendFeedback(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions,
            @NonNull final Callback<Void, Error> callback);

    void cancel();

    interface Callback<R, E> {

        void failure(E error);

        void success(R result);

        void cancelled();
    }

}
