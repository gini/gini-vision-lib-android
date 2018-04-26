package net.gini.android.vision.network;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;

import java.util.LinkedHashMap;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public interface GiniVisionNetworkService {

    CancellationToken upload(@NonNull final Document document,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback);

    CancellationToken delete(@NonNull final String documentId,
            @NonNull final GiniVisionNetworkCallback<Result, Error> callback);

    CancellationToken analyze(@NonNull final LinkedHashMap<String, Integer> documentIdRotationMap,
            @NonNull final GiniVisionNetworkCallback<AnalysisResult, Error> callback);

}
