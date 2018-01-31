package net.gini.android.vision.component.review;

import static android.app.Activity.RESULT_OK;

import static net.gini.android.vision.component.review.compat.ReviewExampleAppCompatActivity.EXTRA_IN_DOCUMENT;
import static net.gini.android.vision.example.ExampleUtil.getExtractionsBundle;
import static net.gini.android.vision.example.ExampleUtil.hasNoPay5Extractions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.component.ExtractionsActivity;
import net.gini.android.vision.component.R;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.review.ReviewFragmentInterface;
import net.gini.android.vision.review.ReviewFragmentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Contains the logic for the Review Screen.
 * <p>
 * Code that differs between the standard and the compatibility library is abstracted away and is
 * implemented in the {@code standard} and {@code compat} packages.
 */
public abstract class BaseReviewScreenHandler implements ReviewFragmentListener {

    private static final int ANALYSIS_REQUEST = 1;
    private static final Logger LOG = LoggerFactory.getLogger(BaseReviewScreenHandler.class);
    private final Activity mActivity;
    private Document mDocument;

    protected BaseReviewScreenHandler(final Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Should analyze document in the Review Screen {}", document);
        // WIP: networking library poc
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document) {
        onProceedToAnalysisScreen(document, null);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document,
            @Nullable final String errorMessage) {
        final Intent intent = getAnalysisActivityIntent(document, errorMessage);
        mActivity.startActivityForResult(intent, ANALYSIS_REQUEST);
    }

    protected abstract Intent getAnalysisActivityIntent(final Document document,
            final String errorMessage);

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull final Document document) {
        LOG.debug("Reviewed and analyzed document {}", document);
        // WIP: networking library poc
    }

    private void showExtractions(final Map<String, GiniVisionSpecificExtraction> extractions) {
        LOG.debug("Show extractions");
        // If we have no Pay 5 extractions we query the Gini Vision Library
        // whether we should show the the Gini Vision No Results Screen
        if (hasNoPay5Extractions(extractions.keySet())
                && GiniVisionCoordinator.shouldShowGiniVisionNoResultsScreen(mDocument)) {
            // Show a special screen, if no Pay5 extractions were found to give the user some
            // hints and tips
            // for using the Gini Vision Library
            showNoResultsScreen(mDocument);
        } else {
            final Intent intent = new Intent(mActivity, ExtractionsActivity.class);
            intent.putExtra(ExtractionsActivity.EXTRA_IN_EXTRACTIONS,
                    getExtractionsBundle(extractions));
            mActivity.startActivity(intent);
            mActivity.setResult(RESULT_OK);
            mActivity.finish();
        }
    }

    private void showNoResultsScreen(final Document document) {
        final Intent intent = getNoResultsActivityIntent(document);
        mActivity.startActivity(intent);
        mActivity.setResult(RESULT_OK);
        mActivity.finish();
    }

    protected abstract Intent getNoResultsActivityIntent(final Document document);

    @Override
    public void onDocumentWasRotated(@NonNull final Document document, final int oldRotation,
            final int newRotation) {
        // WIP: networking library poc
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        Toast.makeText(mActivity, mActivity.getString(R.string.gini_vision_error,
                error.getErrorCode(), error.getMessage()), Toast.LENGTH_LONG).show();
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Document getDocument() {
        return mDocument;
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case ANALYSIS_REQUEST:
                if (resultCode == RESULT_OK) {
                    mActivity.setResult(RESULT_OK);
                    mActivity.finish();
                }
                break;
        }
    }

    public void onCreate(final Bundle savedInstanceState) {
        setUpActionBar();
        setTitles();
        readDocumentFromExtras();

        if (savedInstanceState == null) {
            createReviewFragment();
            showReviewFragment();
        } else {
            retrieveReviewFragment();
        }
    }

    private void readDocumentFromExtras() {
        mDocument = mActivity.getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
    }

    protected abstract ReviewFragmentInterface createReviewFragment();

    protected abstract void showReviewFragment();

    protected abstract ReviewFragmentInterface retrieveReviewFragment();

    protected abstract void setTitles();

    protected abstract void setUpActionBar();

    @Override
    public void onExtractionsAvailable(
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        showExtractions(extractions);
    }

    @Override
    public void onProceedToNoExtractionsScreen(@NonNull final Document document) {
        showNoResultsScreen(document);
    }
}
