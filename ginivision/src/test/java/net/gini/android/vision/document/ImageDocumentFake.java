package net.gini.android.vision.document;

import android.content.Context;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 13.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class ImageDocumentFake extends ImageDocument {

    public RuntimeException failWithException = null;

    public ImageDocumentFake() {
        super(new byte[42], Document.Source.newCameraSource(), Document.ImportMethod.NONE);
    }

    @Override
    public synchronized void loadData(@NonNull final Context context,
            @NonNull final AsyncCallback<byte[], Exception> callback) {
        if (failWithException != null) {
            callback.onError(failWithException);
        } else {
            callback.onSuccess(getData());
        }

    }
}
