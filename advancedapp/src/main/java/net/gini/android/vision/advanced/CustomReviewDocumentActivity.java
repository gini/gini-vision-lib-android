package net.gini.android.vision.advanced;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.reviewdocument.ReviewDocumentFragmentListener;
import net.gini.android.vision.reviewdocument.ReviewDocumentFragmentStandard;
import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.visionadvtest.R;

public class CustomReviewDocumentActivity extends Activity implements ReviewDocumentFragmentListener {

    ReviewDocumentFragmentStandard mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_review_document);
        createFragment();
        showFragment();
    }

    private void createFragment() {
        mFragment = ReviewDocumentFragmentStandard.createInstance(Photo.fromJpeg(new byte[]{}, 0));
    }

    private void showFragment() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_review_document, mFragment)
                .commit();
    }

    @Override
    public void onShouldAnalyzeDocument(Document document) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragment.onPhotoAnalyzed();
                Toast.makeText(CustomReviewDocumentActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 3000);
    }

    @Override
    public void onProceedToAnalyzeScreen(Document document) {
        Toast.makeText(this, "Should proceed to Analyze Screen", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(Document document) {
        Toast.makeText(this, "Photo extractions received", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(GiniVisionError error) {

    }
}
