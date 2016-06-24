package net.gini.android.vision.easy;

import android.content.Intent;
import android.os.Handler;

import net.gini.android.vision.scanner.Document;

public class AnalysisActivity extends net.gini.android.vision.analysis.AnalysisActivity {

    @Override
    public void onAnalyzeDocument(Document document) {
        startScanAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanAnimation();
                onDocumentAnalyzed();
            }
        }, 1000);
    }

    @Override
    public void onAddDataToResult(Intent result) {
        // TODO: add extractions to result
        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, "extractions from analysis screen");
    }
}
