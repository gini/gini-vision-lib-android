package net.gini.android.vision.easy;

import android.os.Handler;
import android.widget.Toast;

import net.gini.android.vision.scanner.photo.Photo;

public class ReviewPhotoActivity extends net.gini.android.vision.reviewphoto.ReviewPhotoActivity {

    @Override
    public void onPhotoReviewedAndAnalyzed(Photo photo) {
        // TODO: show extraction result
        Toast.makeText(this, "Photo extractions received", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onShouldAnalyzePhoto(Photo photo) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setPhotoWasAnalyzed(true);
                Toast.makeText(ReviewPhotoActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }
}
