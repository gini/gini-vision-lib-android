package net.gini.android.vision.network;

/**
 * Created by Alpar Szotyori on 22.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public interface GiniVisionNetworkCallback<R, E> {

    void failure(E error);

    void success(R result);

    void cancelled();
}
