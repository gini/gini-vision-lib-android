package net.gini.android.vision.component.review;

import static android.app.Activity.RESULT_OK;

import static net.gini.android.vision.component.review.compat.ReviewExampleAppCompatActivity.EXTRA_IN_DOCUMENT;
import static net.gini.android.vision.example.ExampleUtil.getExtractionsBundle;
import static net.gini.android.vision.example.ExampleUtil.getLegacyExtractionsBundle;
import static net.gini.android.vision.example.ExampleUtil.hasNoPay5Extractions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.component.ExtractionsActivity;
import net.gini.android.vision.component.R;
import net.gini.android.vision.example.BaseExampleApp;
import net.gini.android.vision.example.DocumentAnalyzer;
import net.gini.android.vision.example.SingleDocumentAnalyzer;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;
import net.gini.android.vision.review.ReviewFragmentInterface;
import net.gini.android.vision.review.ReviewFragmentListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
    private String mDocumentAnalysisErrorMessage;
    private Map<String, SpecificExtraction> mExtractions;
    private ReviewFragmentInterface mReviewFragmentInterface;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    protected BaseReviewScreenHandler(final Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Should analyze document in the Review Screen {}", document);
        GiniVisionDebug.writeDocumentToFile(mActivity, document, "_for_review");

        // We should start analyzing the document by sending it to the Gini API.
        // If the user did not modify the image we can get the analysis results earlier.
        // The Gini Vision Library will not request you to proceed to the Analysis Screen, if the
        // results were
        // received in the Review Screen.
        // If the user modified the image or the analysis didn't complete or it failed the Gini
        // Vision Library
        // will request you to proceed to the Analysis Screen.
        getSingleDocumentAnalyzer().analyzeDocument(document,
                new DocumentAnalyzer.Listener() {
                    @Override
                    public void onException(final Exception exception) {
                        String message = mActivity.getString(R.string.unknown_error);
                        if (exception.getMessage() != null) {
                            message = exception.getMessage();
                        }
                        // Don't show the error message here, but forward it to the Analysis
                        // Fragment, where it will be
                        // shown in a Snackbar
                        mDocumentAnalysisErrorMessage = mActivity.getString(
                                R.string.analysis_failed, message);
                        LOG.error("Analysis failed in the Review Screen", exception);
                    }

                    @Override
                    public void onExtractionsReceived(
                            final Map<String, SpecificExtraction> extractions) {
                        LOG.debug("Document analyzed in the Review Screen");
                        // Cache the extractions until the user clicks the next button and
                        // onDocumentReviewedAndAnalyzed()
                        // will have been called
                        mExtractions = extractions;
                        // Calling onDocumentAnalyzed() is important to notify the Review
                        // Fragment that the
                        // analysis has completed successfully
                        mReviewFragmentInterface.onDocumentAnalyzed();
                    }
                });
    }

    private SingleDocumentAnalyzer getSingleDocumentAnalyzer() {
        if (mSingleDocumentAnalyzer == null) {
            mSingleDocumentAnalyzer =
                    ((BaseExampleApp) mActivity.getApplication()).getSingleDocumentAnalyzer();
        }
        return mSingleDocumentAnalyzer;
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
        // If we have received the extractions while in the Review Screen we don't need to go to
        // the Analysis Screen,
        // we can show the extractions
        if (mExtractions != null) {
            // If we have no Pay 5 extractions we query the Gini Vision Library
            // whether we should show the the Gini Vision No Results Screen
            if (hasNoPay5Extractions(mExtractions.keySet())
                    && GiniVisionCoordinator.shouldShowGiniVisionNoResultsScreen(document)) {
                // Show a special screen, if no Pay5 extractions were found to give the user some
                // hints and tips
                // for using the Gini Vision Library
                showNoResultsScreen(document);
            } else {
                showExtractions(getSingleDocumentAnalyzer().getGiniApiDocument(),
                        getLegacyExtractionsBundle(mExtractions));

            }
            mExtractions = null;
        }
    }

    private void showExtractions(final net.gini.android.models.Document giniApiDocument,
            final Bundle extractionsBundle) {
        LOG.debug("Show extractions");
        final Intent intent = new Intent(mActivity, ExtractionsActivity.class);
        intent.putExtra(ExtractionsActivity.EXTRA_IN_EXTRACTIONS, extractionsBundle);
        mActivity.startActivity(intent);
        mActivity.setResult(RESULT_OK);
        mActivity.finish();
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
        getSingleDocumentAnalyzer().cancelAnalysis();
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
        showExtractions(null, getExtractionsBundle(extractions));
    }

    @Override
    public void onProceedToNoExtractionsScreen(@NonNull final Document document) {
        showNoResultsScreen(document);
    }
}
