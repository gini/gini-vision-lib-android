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

    private boolean mDocumentWasModified = false;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSingleDocumentAnalyzer = ((ScreenApiApp) getApplication()).getSingleDocumentAnalyzer();
    }

    @Override
    public void onAddDataToResult(@NonNull Intent result) {
        LOG.debug("Add data to result");
        // We should add the extraction results here to the Intent
        // We retrieve them when the CameraActivity has finished
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

        // We should start analyzing the document by sending it to the Gini API
        // If the user did not modify the image we can get the analysis results earlier
        // and the Gini Vision Library does not go to the Analysis Screen
        // If the user modified the image or the analysis failed the Gini Vision Library goes to the Analysis Screen
        analyzeDocument(document);
    }

    @Override
    public void onDocumentWasRotated(@NonNull Document document, int oldRotation, int newRotation) {
        super.onDocumentWasRotated(document, oldRotation, newRotation);
        LOG.debug("Document was rotated");
        mSingleDocumentAnalyzer.cancelAnalysis();
    }

    private void analyzeDocument(Document document) {
        mSingleDocumentAnalyzer.analyzeDocument(document, new SingleDocumentAnalyzer.DocumentAnalysisListener() {
            @Override
            public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                mExtractions = extractions;
                onDocumentAnalyzed();
            }

            @Override
            public void onException(Exception exception) {
                if (exception != null) {
                    onDocumentAnalysisError("Analysis failed: " + exception.getMessage());
                }
            }
        });
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        LOG.debug("Proceed to analysis screen");
        mSingleDocumentAnalyzer.removeListener();
        super.onProceedToAnalysisScreen(document);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LOG.debug("Back pressed");
        mSingleDocumentAnalyzer.cancelAnalysis();
    }
}

