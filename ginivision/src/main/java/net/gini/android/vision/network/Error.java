package net.gini.android.vision.network;

/**
 * Created by Alpar Szotyori on 29.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class Error {

    private final String mMessage;

    public Error(final String message) {
        mMessage = message;
    }

    public String getMessage() {
        return mMessage;
    }
}
