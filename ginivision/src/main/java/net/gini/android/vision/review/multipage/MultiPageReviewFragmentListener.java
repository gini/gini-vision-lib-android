package net.gini.android.vision.review.multipage;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;

/**
 * Created by Alpar Szotyori on 07.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Interface used by the {@link MultiPageReviewFragment} to dispatch events to the hosting
 * Activity.
 */
public interface MultiPageReviewFragmentListener {

    /**
     * Called when all pages were uploaded successfully and the user tapped on the "next" button.
     *
     * <p> If you use the Screen API you should start the {@link AnalysisActivity} and set the
     * document as the {@link AnalysisActivity#EXTRA_IN_DOCUMENT} extra.
     *
     * <p> If you use the Component API you should start the {@link AnalysisFragmentCompat} (or the
     * {@link AnalysisFragmentStandard}) and pass the document when creating it with {@link
     * AnalysisFragmentCompat#createInstance(Document, String)} (or {@link
     * AnalysisFragmentStandard#createInstance(Document, String)}).
     *
     * @param document contains the reviewed image (can be the original one or a modified image)
     */
    void onProceedToAnalysisScreen(@NonNull GiniVisionMultiPageDocument document);
}
