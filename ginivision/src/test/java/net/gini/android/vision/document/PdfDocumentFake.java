package net.gini.android.vision.document;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 13.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
public class PdfDocumentFake extends PdfDocument {

    public PdfDocumentFake() {
        super(new Intent(), Uri.EMPTY, Document.Source.newExternalSource(),
                Document.ImportMethod.PICKER);
    }

    @Override
    public synchronized void loadData(@NonNull final Context context,
            @NonNull final AsyncCallback<byte[], Exception> callback) {
        callback.onSuccess(new byte[42]);
    }

}
