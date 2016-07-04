package net.gini.android.vision.component;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.review.ReviewFragmentCompat;
import net.gini.android.vision.review.ReviewFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomReviewAppCompatActivity extends AppCompatActivity implements ReviewFragmentListener {

    ReviewFragmentCompat mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_review_compat);
        createFragment();
        showFragment();
    }

    private void createFragment() {
        mFragment = ReviewFragmentCompat.createInstance(Document.fromPhoto(Photo.fromJpeg(new byte[]{}, 0)));
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_review_document, mFragment)
                .commit();
    }

    @Override
    public void onShouldAnalyzeDocument(Document document) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragment.onDocumentAnalyzed();
                Toast.makeText(CustomReviewAppCompatActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    @Override
    public void onProceedToAnalyzeScreen(Document document) {
        Intent intent = new Intent(this, CustomAnalysisAppCompatActivity.class);
        intent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, document);
        startActivity(intent);
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(Document document) {
        Toast.makeText(this, "Photo extractions received", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(GiniVisionError error) {

    }
}
