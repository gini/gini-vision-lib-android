package net.gini.android.vision.screen;

import static net.gini.android.vision.example.ExampleUtil.getLegacyExtractionsBundle;
import static net.gini.android.vision.example.ExampleUtil.hasNoPay5Extractions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionDebug;
import net.gini.android.vision.example.BaseExampleApp;
import net.gini.android.vision.example.DocumentAnalyzer;
import net.gini.android.vision.example.SingleDocumentAnalyzer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Implements callbacks required by the Gini Vision Library's {@link net.gini.android.vision.analysis.AnalysisActivity}
 * to perform document analysis using the Gini API SDK.
 */
public class AnalysisActivity extends net.gini.android.vision.analysis.AnalysisActivity {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisActivity.class);

    private Map<String, SpecificExtraction> mExtractions;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    @Override
    public void onAddDataToResult(@NonNull final Intent result) {
        LOG.debug("Add data to result");
        // We add the extraction results here to the Intent. The payload format is up to you.
        // For the example we add the extractions as key-value pairs to a Bundle
        // We retrieve them when the CameraActivity has finished in MainActivity#onActivityResult()
        final Bundle extractionsBundle = getLegacyExtractionsBundle(mExtractions);
        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
    }

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Analyze document");
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_analysis");

        startScanAnimation();
        // We can start analyzing the document by sending it to the Gini API
        mSingleDocumentAnalyzer.analyzeDocument(document,
                new DocumentAnalyzer.Listener() {
                    @Override
                    public void onException(final Exception exception) {
                        stopScanAnimation();
                        String message = "Analysis failed: ";
                        if (exception != null) {
                            message += exception.getMessage();
                        }
                        final DocumentAnalyzer.Listener listener = this;
                        showError(message, getString(net.gini.android.vision.R.string.gv_document_analysis_error_retry),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        startScanAnimation();
                                        mSingleDocumentAnalyzer.cancelAnalysis();
                                        mSingleDocumentAnalyzer.analyzeDocument(document, listener);
                                    }
                                });
                    }

                    @Override
                    public void onExtractionsReceived(
                            final Map<String, SpecificExtraction> extractions) {
                        mExtractions = extractions;
                        if (mExtractions == null || hasNoPay5Extractions(mExtractions.keySet())) {
                            onNoExtractionsFound();
                        } else {
                            // Calling onDocumentAnalyzed() is important to notify the
                            // AnalysisActivity
                            // base class that the
                            // analysis has completed successfully
                            onDocumentAnalyzed();
                        }
                    }
                });
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSingleDocumentAnalyzer = ((BaseExampleApp) getApplication()).getSingleDocumentAnalyzer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((BaseExampleApp) getApplication()).getSingleDocumentAnalyzer().cancelAnalysis();
    }

}
