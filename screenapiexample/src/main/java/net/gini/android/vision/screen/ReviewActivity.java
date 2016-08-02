package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionDebug;

public class ReviewActivity extends net.gini.android.vision.review.ReviewActivity {

    @Override
    public void onAddDataToResult(@NonNull Intent result) {
        // We should add the extraction results here to the Intent
        // We retrieve them when the CameraActivity has finished
        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, "extractions from review screen");
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
        GiniVisionDebug.writeDocumentToFile(this,document,"_for_review");

        // We should start analyzing the document by sending it to the Gini API
        // If the user does not modify the image we can get the analysis results earlier
        // Currently we only simulate analysis and we tell the ReviewActivity that the
        // document was analyzed after 2000 ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onDocumentAnalyzed();
                Toast.makeText(ReviewActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }
}
