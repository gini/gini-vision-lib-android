package net.gini.android.vision.component.review.compat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.component.ExtractionsActivity;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.compat.AnalysisExampleAppCompatActivity;
import net.gini.android.vision.component.noresults.compat.NoResultsExampleAppCompatActivity;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.review.ReviewFragmentCompat;
import net.gini.android.vision.review.ReviewFragmentListener;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * AppCompatActivity using the {@link ReviewScreenHandlerAppCompat} to host the
 * {@link ReviewFragmentCompat} and to start the {@link AnalysisExampleAppCompatActivity}, the
 * {@link NoResultsExampleAppCompatActivity} or the {@link ExtractionsActivity}.
 */
public class ReviewExampleAppCompatActivity extends AppCompatActivity implements
        ReviewFragmentListener {

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";
    public static final int RESULT_ADD_MORE_PAGES = RESULT_FIRST_USER + 3001;
    public static final String EXTRA_OUT_MULTI_PAGE_FIRST_PAGE = "EXTRA_OUT_MULTI_PAGE_FIRST_PAGE";

    private ReviewScreenHandlerAppCompat mReviewScreenHandler;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mReviewScreenHandler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_compat);
        mReviewScreenHandler = new ReviewScreenHandlerAppCompat(this);
        mReviewScreenHandler.onCreate(savedInstanceState);
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {
        mReviewScreenHandler.onShouldAnalyzeDocument(document);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document) {
        mReviewScreenHandler.onProceedToAnalysisScreen(document);
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {
        mReviewScreenHandler.onDocumentReviewedAndAnalyzed(document);
    }

    @Override
    public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation,
            final int newRotation) {
        mReviewScreenHandler.onDocumentWasRotated(document, oldRotation, newRotation);
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        mReviewScreenHandler.onError(error);
    }

    @Override
    public void onExtractionsAvailable(
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        mReviewScreenHandler.onExtractionsAvailable(extractions);
    }

    @Override
    public void onProceedToNoExtractionsScreen(@NonNull final Document document) {
        mReviewScreenHandler.onProceedToNoExtractionsScreen(document);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document,
            @Nullable final String errorMessage) {
        mReviewScreenHandler.onProceedToAnalysisScreen(document, errorMessage);
    }

    public static Intent newInstance(final Document document, final Context context) {
        final Intent intent = new Intent(context, ReviewExampleAppCompatActivity.class);
        intent.putExtra(ReviewExampleAppCompatActivity.EXTRA_IN_DOCUMENT, document);
        return intent;
    }
}
