package net.gini.android.vision.review.multipage.previews;

import android.support.annotation.NonNull;

import net.gini.android.vision.document.ImageDocument;

/**
 * Created by Alpar Szotyori on 11.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * @exclude
 */
public interface PreviewFragmentListener {

    void onRetryUpload(@NonNull final ImageDocument document);

    void onDeleteDocument(@NonNull final ImageDocument document);
}
