package net.gini.android.vision.easy;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import net.gini.android.vision.scanner.photo.Photo;

public class ReviewPhotoActivity extends net.gini.android.vision.reviewphoto.ReviewPhotoActivity {

    public static final String EXTRA_OUT_EXTRACTIONS = "EXTRA_OUT_EXTRACTIONS";

    @Override
    public void onAddDataToResult(Intent result) {
        // TODO: add extractions to result
        result.putExtra(EXTRA_OUT_EXTRACTIONS, "extractions");
    }

    @Override
    public void onShouldAnalyzePhoto(Photo photo) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onPhotoAnalyzed();
                Toast.makeText(ReviewPhotoActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }
}
