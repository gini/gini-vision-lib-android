package net.gini.android.vision.review.multipage.previews;

import android.support.annotation.NonNull;

import net.gini.android.vision.document.GiniVisionDocumentError;

/**
 * Created by Alpar Szotyori on 14.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public interface PreviewsAdapterListener {

    PreviewFragment.ErrorButtonAction getErrorButtonAction(
            @NonNull final GiniVisionDocumentError documentError);
}
