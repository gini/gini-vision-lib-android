package net.gini.android.vision.internal.document;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.document.ImageMultiPageDocument;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * @exclude
 */
public class ImageMultiPageDocumentMemoryStore {

    private ImageMultiPageDocument mMultiPageDocument;

    public void setMultiPageDocument(@NonNull final ImageMultiPageDocument multiPageDocument) {
        mMultiPageDocument = multiPageDocument;
    }

    @Nullable
    public ImageMultiPageDocument getMultiPageDocument() {
        return mMultiPageDocument;
    }

    public void clear() {
        mMultiPageDocument = null;
    }
}
