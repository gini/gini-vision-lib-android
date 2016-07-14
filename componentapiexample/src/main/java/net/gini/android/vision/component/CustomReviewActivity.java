package net.gini.android.vision.component;

import static net.gini.android.vision.component.Util.readAsset;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.review.ReviewFragmentListener;
import net.gini.android.vision.review.ReviewFragmentStandard;
import net.gini.android.visionadvtest.R;

public class CustomReviewActivity extends Activity implements ReviewFragmentListener {

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";

    private ReviewFragmentStandard mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_review);
        createFragment();
        showFragment();
    }

    private void createFragment() {
        Document document = getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
        if (document == null) {
            document = Document.fromPhoto(Photo.fromJpeg(readAsset(this, "test_document.jpg"), 0));
        }
        mFragment = ReviewFragmentStandard.createInstance(document);
    }

    private void showFragment() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_review_document, mFragment)
                .commit();
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragment.onDocumentAnalyzed();
                Toast.makeText(CustomReviewActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        Intent intent = new Intent(this, CustomAnalysisActivity.class);
        intent.putExtra(CustomAnalysisActivity.EXTRA_IN_DOCUMENT, document);
        startActivity(intent);
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        Toast.makeText(this, "Photo extractions received", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {

    }
}
