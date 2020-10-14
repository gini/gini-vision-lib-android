package net.gini.android.vision.network;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Used by the {@link GiniVisionNetworkService} and {@link GiniVisionNetworkApi} to return error
 * messages.
 */
public class Error {

    private final String mMessage;
    private final Throwable mCause;

    /**
     * Create a new error.
     *
     * @param message error message
     */
    public Error(@NonNull final String message) {
        mMessage = message;
        mCause = null;
    }

    /**
     * Create a new error with a cause.
     *
     * @param message error message
     * @param cause the cause of the error
     */
    public Error(@NonNull final String message, @NonNull final Throwable cause) {
        mMessage = message;
        mCause = cause;
    }

    /**
     * @return error message
     */
    @NonNull
    public String getMessage() {
        return mMessage;
    }

    /**
     * @return error cause
     */
    @Nullable
    public Throwable getCause() {
        return mCause;
    }
}
