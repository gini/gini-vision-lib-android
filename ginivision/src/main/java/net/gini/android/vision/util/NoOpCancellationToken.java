package net.gini.android.vision.util;

/**
 * Created by Alpar Szotyori on 24.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class NoOpCancellationToken implements CancellationToken {

    @Override
    public void cancel() {
    }
}
