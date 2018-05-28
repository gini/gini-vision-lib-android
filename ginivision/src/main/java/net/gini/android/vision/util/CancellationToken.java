package net.gini.android.vision.util;

/**
 * Created by Alpar Szotyori on 26.04.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Used to allow requesting cancellation of asynchronous tasks.
 */
public interface CancellationToken {

    void cancel();
}
