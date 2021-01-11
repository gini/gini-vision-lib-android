package net.gini.android.vision.screen;

import static net.gini.android.vision.example.shared.ExampleUtil.getLegacyExtractionsBundle;
import static net.gini.android.vision.example.shared.ExampleUtil.hasNoPay5Extractions;

import android.content.Intent;
import android.os.Bundle;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.example.shared.BaseExampleApp;
import net.gini.android.vision.example.shared.DocumentAnalyzer;
import net.gini.android.vision.example.shared.SingleDocumentAnalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Implements callbacks required by the Gini Vision Library's {@link net.gini.android.vision.review.ReviewActivity}
 * to perform document analysis using the Gini API SDK.
 */
public class ReviewActivity extends net.gini.android.vision.review.ReviewActivity {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewActivity.class);

    private Map<String, SpecificExtraction> mExtractions;

    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    private SingleDocumentAnalyzer getSingleDocumentAnalyzer() {
        if (mSingleDocumentAnalyzer == null) {
            mSingleDocumentAnalyzer =
                    ((BaseExampleApp) getApplication()).getSingleDocumentAnalyzer();
        }
        return mSingleDocumentAnalyzer;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAddDataToResult(@NonNull final Intent result) {
        LOG.debug("Add data to result");
        // We add the extraction results here to the Intent. The payload format is up to you.
        // For the example we add the extractions as key-value pairs to a Bundle
        // We retrieve them when the CameraActivity has finished in MainActivity#onActivityResult()
        final Bundle extractionsBundle = getLegacyExtractionsBundle(mExtractions);
        if (extractionsBundle != null) {
            result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
        }
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Should analyze document");
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_review");
        // We should start analyzing the document by sending it to the Gini API.
        // If the user did not modify the image we can get the analysis results earlier.
        // The Gini Vision Library does not go to the Analysis Screen, if the results were received in the Review Screen.
        // If the user modified the image or the analysis didn't complete or it failed the Gini Vision Library
        // goes to the Analysis Screen.
        analyzeDocument(document);
    }

    @Override
    public void onDocumentWasRotated(
            @NonNull final Document document, final int oldRotation, final int newRotation) {
        super.onDocumentWasRotated(document, oldRotation, newRotation);
        LOG.debug("Document was rotated");
        // We need to cancel the analysis here, we will have to upload the rotated document in the Analysis Screen
        getSingleDocumentAnalyzer().cancelAnalysis();
    }

    private void analyzeDocument(final Document document) {
        getSingleDocumentAnalyzer().cancelAnalysis();
        getSingleDocumentAnalyzer().analyzeDocument(document, new DocumentAnalyzer.Listener() {
            @Override
            public void onExtractionsReceived(final Map<String, SpecificExtraction> extractions) {
                mExtractions = extractions;
                if (mExtractions == null || hasNoPay5Extractions(mExtractions.keySet())) {
                    onNoExtractionsFound();
                } else {
                    // Calling onDocumentAnalyzed() is important to notify the
                    // ReviewActivity base class that the analysis has completed successfully
                    onDocumentAnalyzed();
                }
            }

            @Override
            public void onException(final Exception exception) {
                if (exception != null) {
                    String message = "unknown";
                    if (exception.getMessage() != null) {
                        message = exception.getMessage();
                    }
                    // Provide an error message which will be shown in the Analysis Screen with a retry button
                    onDocumentAnalysisError("Analysis failed: " + message);
                }
            }
        });
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull final Document document) {
        LOG.debug("Proceed to analysis screen");
        // As the library will go to the Analysis Screen we should only remove the listener.
        // We should not cancel the analysis here as we don't know, if we proceed because the analysis didn't complete or
        // the user rotated the image
        getSingleDocumentAnalyzer().removeListener();
        super.onProceedToAnalysisScreen(document);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LOG.debug("Back pressed");
        // Cancel the analysis here, this method is called when the user presses back button or the up button in the
        // ActionBar
        getSingleDocumentAnalyzer().cancelAnalysis();
    }
}

