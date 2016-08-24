package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import net.gini.android.ginivisiontest.R;
import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionDebug;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AnalysisActivity extends net.gini.android.vision.analysis.AnalysisActivity {

    private static final Logger LOG = LoggerFactory.getLogger(AnalysisActivity.class);

    private Map<String, SpecificExtraction> mExtractions;
    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSingleDocumentAnalyzer = ((ScreenApiApp) getApplication()).getSingleDocumentAnalyzer();
    }

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        LOG.debug("Analyze document");
        GiniVisionDebug.writeDocumentToFile(this, document, "_for_analysis");

        startScanAnimation();
        // We can start analyzing the document by sending it to the Gini API
        mSingleDocumentAnalyzer.analyzeDocument(document, new SingleDocumentAnalyzer.DocumentAnalysisListener() {
            @Override
            public void onExtractionsReceived(Map<String, SpecificExtraction> extractions) {
                mExtractions = extractions;
                    // Calling onDocumentAnalyzed() is important to notify the AnalysisActivity base class that the
                    // analysis has completed successfully
                onDocumentAnalyzed();
            }

            @Override
            public void onException(Exception exception) {
                stopScanAnimation();
                String message = "Analysis failed: ";
                if (exception != null) {
                   message += exception.getMessage();
                }
                final SingleDocumentAnalyzer.DocumentAnalysisListener listener = this;
                showError(message, getString(R.string.gv_document_analysis_error_retry), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startScanAnimation();
                        mSingleDocumentAnalyzer.cancelAnalysis();
                        mSingleDocumentAnalyzer.analyzeDocument(document, listener);
                    }
                });
            }
        });
    }

    @Override
    public void onAddDataToResult(@NonNull Intent result) {
        LOG.debug("Add data to result");
        // We add the extraction results here to the Intent. The payload format is up to you.
        // For the example we add the extractions as key-value pairs to a Bundle
        // We retrieve them when the CameraActivity has finished in MainActivity#onActivityResult()
        Bundle extractionsBundle = getExtractionsBundle();
        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((ScreenApiApp) getApplication()).getSingleDocumentAnalyzer().cancelAnalysis();
    }

    private Bundle getExtractionsBundle() {
        final Bundle extractionsBundle = new Bundle();
        for (Map.Entry<String, SpecificExtraction> entry : mExtractions.entrySet()) {
            extractionsBundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return extractionsBundle;
    }
}
