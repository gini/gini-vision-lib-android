package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import net.gini.android.vision.Document;

public class AnalysisActivity extends net.gini.android.vision.analysis.AnalysisActivity {

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        startScanAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopScanAnimation();
                showError("Oh my dear, something seems to have gone terribly wrong!", "Be so kind and fix it", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onDocumentAnalyzed();
                        hideError();
                    }
                });
            }
        }, 3000);
    }

    @Override
    public void onAddDataToResult(Intent result) {
        // TODO: add extractions to result
        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, "extractions from analysis screen");
    }
}
