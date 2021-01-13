package net.gini.android.vision.internal.document;

import net.gini.android.vision.document.ImageMultiPageDocument;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
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
        mMultiPageDocument = null; // NOPMD
    }
}
