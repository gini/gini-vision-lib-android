package net.gini.android.vision.advanced;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import net.gini.android.vision.reviewphoto.ReviewPhotoFragmentListener;
import net.gini.android.vision.reviewphoto.ReviewPhotoFragmentStandard;
import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.visionadvtest.R;

public class CustomReviewPhotoActivity extends Activity implements ReviewPhotoFragmentListener {

    ReviewPhotoFragmentStandard mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_review_photo);
        bindViews();
    }

    private void bindViews() {
        mFragment = (ReviewPhotoFragmentStandard) getFragmentManager().findFragmentById(R.id.fragment_review_photo);
    }

    @Override
    public void onShouldAnalyzePhoto(Photo photo) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragment.setPhotoWasAnalyzed(true);
                Toast.makeText(CustomReviewPhotoActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    @Override
    public void onProceedToAnalyzePhotoScreen(Photo photo) {
        Toast.makeText(this, "Should proceed to Analyze Screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPhotoReviewedAndAnalyzed(Photo photo) {
        Toast.makeText(this, "Photo extractions received", Toast.LENGTH_SHORT).show();
    }
}
