package net.gini.android.vision.screen;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import net.gini.android.vision.Document;

public class ReviewActivity extends net.gini.android.vision.review.ReviewActivity {

    @Override
    public void onAddDataToResult(Intent result) {
        // TODO: add extractions to result
        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, "extractions from review screen");
    }

    @Override
    public void onShouldAnalyzeDocument(Document document) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onDocumentAnalyzed();
                Toast.makeText(ReviewActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }
}
