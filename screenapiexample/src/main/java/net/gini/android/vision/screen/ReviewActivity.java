package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ReviewActivity extends net.gini.android.vision.review.ReviewActivity {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewActivity.class);

    private Map<String, SpecificExtraction> mExtractions;

    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSingleDocumentAnalyzer = ((ScreenApiApp) getApplication()).getSingleDocumentAnalyzer();
    }

    @Override
    public void onAddDataToResult(@NonNull Intent result) {
        LOG.debug("Add data to result");
        // We add the extraction results here to the Intent. The payload format is up to you.
        // For the example we add the extractions as key-value pairs to a Bundle
        // We retrieve them when the CameraActivity has finished in MainActivity#onActivityResult()
        Bundle extractionsBundle = getExtractionsBundle();
        if (extractionsBundle != null) {
            result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
        }
    }

    private Bundle getExtractionsBundle() {
        if (mExtractions == null) {
            return null;
        }
        final Bundle extractionsBundle = new Bundle();
        for (Map.Entry<String, SpecificExtraction> entry : mExtractions.entrySet()) {
            extractionsBundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return extractionsBundle;
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
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
    public void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation) {
        super.onDocumentWasRotated(document, oldRotation, newRotation);
        LOG.debug("Document was rotated");
        // We need to cancel the analysis here, we will have to upload the rotated document in the Analysis Screen
        mSingleDocumentAnalyzer.cancelAnalysis();
    }

    private void analyzeDocument(Document document) {
        mSingleDocumentAnalyzer.cancelAnalysis();
        mSingleDocumentAnalyzer.analyzeDocument(document, new SingleDocumentAnalyzer.DocumentAnalysisListener() {
            @Override
            public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                mExtractions = extractions;
                // Calling onDocumentAnalyzed() is important to notify the ReviewActivity base class that the
                // analysis has completed successfully
                onDocumentAnalyzed();
            }

            @Override
            public void onException(Exception exception) {
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
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        LOG.debug("Proceed to analysis screen");
        // As the library will go to the Analysis Screen we should only remove the listener.
        // We should not cancel the analysis here as we don't know, if we proceed because the analysis didn't complete or
        // the user rotated the image
        mSingleDocumentAnalyzer.removeListener();
        super.onProceedToAnalysisScreen(document);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LOG.debug("Back pressed");
        // Cancel the analysis here, this method is called when the user presses back button or the up button in the
        // ActionBar
        mSingleDocumentAnalyzer.cancelAnalysis();
    }
}

