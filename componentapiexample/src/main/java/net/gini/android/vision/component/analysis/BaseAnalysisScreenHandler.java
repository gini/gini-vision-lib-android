package net.gini.android.vision.component.analysis;

import static android.app.Activity.RESULT_OK;

import static net.gini.android.vision.component.analysis.compat.AnalysisExampleAppCompatActivity.EXTRA_IN_DOCUMENT;
import static net.gini.android.vision.component.analysis.compat.AnalysisExampleAppCompatActivity.EXTRA_IN_ERROR_MESSAGE;
import static net.gini.android.vision.example.ExampleUtil.getExtractionsBundle;
import static net.gini.android.vision.example.ExampleUtil.hasNoPay5Extractions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisFragmentInterface;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.component.ExtractionsActivity;
import net.gini.android.vision.component.R;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Contains the logic for the Analysis Screen.
 * <p>
 * Code that differs between the standard and the compatibility library is abstracted away and is
 * implemented in the {@code standard} and {@code compat} packages.
 */
public abstract class BaseAnalysisScreenHandler implements AnalysisFragmentListener {

    private static final Logger LOG = LoggerFactory.getLogger(BaseAnalysisScreenHandler.class);

    private final Activity mActivity;
    private AnalysisFragmentInterface mAnalysisFragmentInterface;
    private Document mDocument;
    private String mErrorMessageFromReviewScreen;

    protected BaseAnalysisScreenHandler(final Activity activity) {
        mActivity = activity;
    }

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Analyze document {}", document);
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
            mActivity.setResult(Activity.RESULT_OK);
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
    public void onError(@NonNull final GiniVisionError error) {
        mAnalysisFragmentInterface.showError(mActivity.getString(R.string.gini_vision_error,
                error.getErrorCode(), error.getMessage()), Toast.LENGTH_LONG);
    }

    public Activity getActivity() {
        return mActivity;
    }

    public Document getDocument() {
        return mDocument;
    }

    protected String getErrorMessageFromReviewScreen() {
        return mErrorMessageFromReviewScreen;
    }

    public void onCreate(final Bundle savedInstanceState) {
        setUpActionBar();
        setTitles();
        readExtras();

        if (savedInstanceState == null) {
            mAnalysisFragmentInterface = createAnalysisFragment();
            showAnalysisFragment();
        } else {
            mAnalysisFragmentInterface = retrieveAnalysisFragment();
        }
    }

    protected abstract AnalysisFragmentInterface retrieveAnalysisFragment();

    protected abstract void showAnalysisFragment();

    protected abstract AnalysisFragmentInterface createAnalysisFragment();

    private void readExtras() {
        mDocument = mActivity.getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
        mErrorMessageFromReviewScreen = mActivity.getIntent().getStringExtra(
                EXTRA_IN_ERROR_MESSAGE);
    }

    protected abstract void setTitles();

    protected abstract void setUpActionBar();

    @Override
    public void onExtractionsAvailable(@NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {
        showExtractions(extractions);
    }

    @Override
    public void onProceedToNoExtractionsScreen(@NonNull final Document document) {
        showNoResultsScreen(document);
    }
}
