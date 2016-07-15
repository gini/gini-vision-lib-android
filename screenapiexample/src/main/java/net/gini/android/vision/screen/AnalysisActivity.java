package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;

import net.gini.android.vision.Document;

public class AnalysisActivity extends net.gini.android.vision.analysis.AnalysisActivity {

    @Override
    public void onAnalyzeDocument(@NonNull final Document document) {
        // We can start analyzing the document by sending it to the Gini API
        // Currently we only simulate analysis and show an error after 3000 ms to view the error customizations
        // and when the user presses the button on the error snackbar, we tell the AnalysisActivity that the
        // document was analyzed
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
        // We should add the extraction results here to the Intent
        // We retrieve them when the CameraActivity has finished
        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, "extractions from analysis screen");
    }
}
