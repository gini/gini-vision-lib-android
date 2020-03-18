package net.gini.android.vision.network;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import androidx.annotation.NonNull;

/**
 * Used by the {@link GiniVisionNetworkService} and {@link GiniVisionNetworkApi} to return error
 * messages.
 */
public class Error {

    private final String mMessage;

    /**
     * Create a new error.
     *
     * @param message error message
     */
    public Error(@NonNull final String message) {
        mMessage = message;
    }

    /**
     * @return error message
     */
    public String getMessage() {
        return mMessage;
    }
}
