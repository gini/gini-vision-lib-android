package net.gini.android.vision.reviewphoto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.gini.android.vision.R;
import net.gini.android.vision.scanner.photo.Photo;

public abstract class ReviewPhotoActivity extends AppCompatActivity implements ReviewPhotoFragmentListener {

    private ReviewPhotoFragmentCompat mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_review_photo);
        bindViews();
    }

    private void bindViews() {
        mFragment = (ReviewPhotoFragmentCompat) getSupportFragmentManager().findFragmentById(R.id.gv_fragment_review_photo);
    }

    // callback for subclasses for uploading the photo before it was reviewed, if the photo is not changed
    // no new upload is required
    @Override
    public abstract void onShouldAnalyzePhoto(Photo photo);

    @Override
    public void onProceedToAnalyzePhotoScreen(Photo photo) {
        // TODO: start analyze screen
        Toast.makeText(this, "Should proceed to Analyze Screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public abstract void onPhotoReviewedAndAnalyzed(Photo photo);

    // TODO: call this, if the photo was analyzed before the review was completed, it prevents the analyze activity to
    // be started, if the photo was already analyzed and the user didn't change it
    protected void setPhotoWasAnalyzed(boolean photoWasAnalyzed) {
        mFragment.setPhotoWasAnalyzed(photoWasAnalyzed);
    }
}
