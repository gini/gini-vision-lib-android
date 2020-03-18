package net.gini.android.vision.review.multipage;

import android.content.Context;
import android.content.Intent;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.camera.CameraFragmentCompat;
import net.gini.android.vision.camera.CameraFragmentStandard;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;

import androidx.annotation.NonNull;

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

    /**
     * Called when the user wants to add a picture of another page. Also called when the user has
     * deleted every page and the document consisted of images taken with the Camera Screen or
     * imported using the Camera Screen.
     *
     * <p> If you host the {@link MultiPageReviewFragment} in its own Activity, then you should
     * simply finish the Activity.
     *
     * <p> If you use one Activity to host all the GVL fragments, then you should display the
     * {@link CameraFragmentCompat} (or the {@link CameraFragmentStandard}) again.
     */
    void onReturnToCameraScreen();

    /**
     * Called when the user deleted all the pages of a document received from another app.
     * This means the {@link MultiPageReviewFragment} was launched after a document had been created
     * using {@link GiniVision#createDocumentForImportedFiles(Intent, Context, AsyncCallback)}.
     *
     * <p> At this point you should finish GVL by closing the {@link MultiPageReviewFragment} and
     * cleaning up using {@link GiniVision#cleanup(Context)}.
     */
    void onImportedDocumentReviewCancelled();
}
