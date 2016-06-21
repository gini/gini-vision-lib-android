package net.gini.android.vision.easy;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import net.gini.android.vision.scanner.Document;

public class ReviewDocumentActivity extends net.gini.android.vision.reviewdocument.ReviewDocumentActivity {

    public static final String EXTRA_OUT_EXTRACTIONS = "EXTRA_OUT_EXTRACTIONS";

    @Override
    public void onAddDataToResult(Intent result) {
        // TODO: add extractions to result
        result.putExtra(EXTRA_OUT_EXTRACTIONS, "extractions");
    }

    @Override
    public void onShouldAnalyzeDocument(Document document) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onPhotoAnalyzed();
                Toast.makeText(net.gini.android.vision.easy.ReviewDocumentActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }
}
