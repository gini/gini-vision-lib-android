package net.gini.android.vision.review.multipage;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;

/**
 * Created by Alpar Szotyori on 07.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public interface MultiPageReviewFragmentListener {

    void onAddMorePages(@NonNull Document document);

    void onProceedToAnalysisScreen(@NonNull Document document);
}
