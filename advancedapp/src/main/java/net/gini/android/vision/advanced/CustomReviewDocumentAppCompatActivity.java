package net.gini.android.vision.advanced;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analyse.AnalyseDocumentActivity;
import net.gini.android.vision.reviewdocument.ReviewDocumentFragmentCompat;
import net.gini.android.vision.reviewdocument.ReviewDocumentFragmentListener;
import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.scanner.photo.Photo;
import net.gini.android.visionadvtest.R;

public class CustomReviewDocumentAppCompatActivity extends AppCompatActivity implements ReviewDocumentFragmentListener {

    ReviewDocumentFragmentCompat mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_review_document_compat);
        createFragment();
        showFragment();
    }

    private void createFragment() {
        mFragment = ReviewDocumentFragmentCompat.createInstance(Photo.fromJpeg(new byte[]{}, 0));
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
                Toast.makeText(CustomReviewDocumentAppCompatActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    @Override
    public void onProceedToAnalyzeScreen(Document document) {
        Intent intent = new Intent(this, CustomAnalyseDocumentAppCompatActivity.class);
        intent.putExtra(AnalyseDocumentActivity.EXTRA_IN_DOCUMENT, document);
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
