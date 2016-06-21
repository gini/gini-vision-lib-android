package net.gini.android.vision.easy;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import net.gini.android.vision.scanner.photo.Photo;

public class ReviewPhotoActivity extends net.gini.android.vision.reviewphoto.ReviewPhotoActivity {

    @Override
    public void onAddDataToResult(Intent result) {
        // TODO: add extractions to result
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
