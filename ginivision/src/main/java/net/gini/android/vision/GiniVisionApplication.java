package net.gini.android.vision;

import android.support.annotation.NonNull;

import net.gini.android.vision.network.GiniVisionNetwork;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public interface GiniVisionApplication {

    @NonNull
    GiniVisionNetwork getGiniVisionNetwork();

}
