package net.gini.android.vision.component.analysis;

import static android.app.Activity.RESULT_OK;

import static net.gini.android.vision.component.analysis.AnalysisExampleAppCompatActivity.EXTRA_IN_DOCUMENT;
import static net.gini.android.vision.component.analysis.AnalysisExampleAppCompatActivity.EXTRA_IN_ERROR_MESSAGE;
import static net.gini.android.vision.example.ExampleUtil.hasNoPay5Extractions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisFragmentInterface;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.component.ComponentApiExampleApp;
import net.gini.android.vision.component.ExtractionsActivity;
import net.gini.android.vision.component.R;
import net.gini.android.vision.example.DocumentAnalyzer;
import net.gini.android.vision.example.SingleDocumentAnalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public abstract class AbstractAnalysisScreenHandler implements AnalysisFragmentListener {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractAnalysisScreenHandler.class);

    private final Activity mActivity;
    private AnalysisFragmentInterface mAnalysisFragmentInterface;
    private Document mDocument;
    private String mErrorMessageFromReviewScreen;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    AbstractAnalysisScreenHandler(final Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Analyze document {}", document);
        GiniVisionDebug.writeDocumentToFile(mActivity, document, "_for_analysis");

        mAnalysisFragmentInterface.startScanAnimation();
        // We can start analyzing the document by sending it to the Gini API
        getSingleDocumentAnalyzer().analyzeDocument(document,
                new DocumentAnalyzer.Listener() {
                    @Override
                    public void onException(Exception exception) {
                        mAnalysisFragmentInterface.stopScanAnimation();
                        String message = "unknown";
                        if (exception.getMessage() != null) {
                            message = exception.getMessage();
                        }

                        // Show the error in the Snackbar with a retry button
                        final DocumentAnalyzer.Listener listener = this;
                        mAnalysisFragmentInterface.showError("Analysis failed: " + message,
                                mActivity.getString(R.string.retry_analysis),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mAnalysisFragmentInterface.startScanAnimation();
                                        getSingleDocumentAnalyzer().cancelAnalysis();
                                        getSingleDocumentAnalyzer().analyzeDocument(document,
                                                listener);
                                    }
                                });
                        LOG.error("Analysis failed in the Analysis Screen", exception);
                    }

                    @Override
                    public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                        LOG.debug("Document analyzed in the Analysis Screen");
                        // Calling onDocumentAnalyzed() is important to notify the Analysis
                        // Fragment that the
                        // analysis has completed successfully
                        mAnalysisFragmentInterface.onDocumentAnalyzed();
                        mAnalysisFragmentInterface.stopScanAnimation();
                        showExtractions(getSingleDocumentAnalyzer().getGiniApiDocument(),
                                extractions, document);
                    }
                });
    }

    private SingleDocumentAnalyzer getSingleDocumentAnalyzer() {
        if (mSingleDocumentAnalyzer == null) {
            mSingleDocumentAnalyzer =
                    ((ComponentApiExampleApp) mActivity.getApplication()).getSingleDocumentAnalyzer();
        }
        return mSingleDocumentAnalyzer;
    }

    private void showExtractions(net.gini.android.models.Document giniApiDocument,
            Map<String, SpecificExtraction> extractions, Document document) {
        LOG.debug("Show extractions");
        // If we have no Pay 5 extractions we query the Gini Vision Library
        // whether we should show the the Gini Vision No Results Screen
        if (hasNoPay5Extractions(extractions.keySet())
                && GiniVisionCoordinator.shouldShowGiniVisionNoResultsScreen(document)) {
            // Show a special screen, if no Pay5 extractions were found to give the user some
            // hints and tips
            // for using the Gini Vision Library
            showNoResultsScreen(document);
        } else {
            Intent intent = new Intent(mActivity, ExtractionsActivity.class);
            intent.putExtra(ExtractionsActivity.EXTRA_IN_DOCUMENT, giniApiDocument);
            intent.putExtra(ExtractionsActivity.EXTRA_IN_EXTRACTIONS,
                    getExtractionsBundle(extractions));
            mActivity.startActivity(intent);
            mActivity.setResult(Activity.RESULT_OK);
            mActivity.finish();
        }
    }

    private Bundle getExtractionsBundle(Map<String, SpecificExtraction> extractions) {
        final Bundle extractionsBundle = new Bundle();
        for (Map.Entry<String, SpecificExtraction> entry : extractions.entrySet()) {
            extractionsBundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return extractionsBundle;
    }

    private void showNoResultsScreen(final Document document) {
        final Intent intent = getNoResultsActivityIntent(document);
        mActivity.startActivity(intent);
        mActivity.setResult(RESULT_OK);
        mActivity.finish();
    }

    protected abstract Intent getNoResultsActivityIntent(final Document document);

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        mAnalysisFragmentInterface.showError("Error: " +
                        error.getErrorCode() + " - " +
                        error.getMessage(),
                Toast.LENGTH_LONG);
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Document getDocument() {
        return mDocument;
    }

    String getErrorMessageFromReviewScreen() {
        return mErrorMessageFromReviewScreen;
    }

    void onCreate(final Bundle savedInstanceState) {
        setUpActionBar();
        setTitles();
        readExtras();

        if (savedInstanceState == null) {
            mAnalysisFragmentInterface = showAnalysisFragment();
        } else {
            mAnalysisFragmentInterface = retainAnalysisFragment();
        }
    }

    protected abstract AnalysisFragmentInterface retainAnalysisFragment();

    protected abstract AnalysisFragmentInterface showAnalysisFragment();

    private void readExtras() {
        mDocument = mActivity.getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
        mErrorMessageFromReviewScreen = mActivity.getIntent().getStringExtra(
                EXTRA_IN_ERROR_MESSAGE);
    }

    protected abstract void setTitles();

    protected abstract void setUpActionBar();

}
