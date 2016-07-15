package net.gini.android.vision.component;

import static net.gini.android.vision.component.Util.readAsset;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.review.ReviewFragmentCompat;
import net.gini.android.vision.review.ReviewFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomReviewAppCompatActivity extends AppCompatActivity implements ReviewFragmentListener {

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";

    private ReviewFragmentCompat mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_review_compat);
        // The ReviewFragment cannot be directly added to a layout, because it requires a Document
        createFragment();
        showFragment();
    }

    private void createFragment() {
        Document document = getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
        if (document == null) {
            document = Document.fromPhoto(Photo.fromJpeg(readAsset(this, "test_document.jpg"), 0));
        }
        mFragment = ReviewFragmentCompat.createInstance(document);
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_review_document, mFragment)
                .commit();
    }

    @Override
    public void onShouldAnalyzeDocument(@NonNull Document document) {
        // We should start analyzing the document by sending it to the Gini API
        // If the user does not modify the image we can get the analysis results earlier
        // Currently we only simulate analysis and we tell the ReviewFragment that the
        // document was analyzed after 2000 ms
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragment.onDocumentAnalyzed();
                Toast.makeText(CustomReviewAppCompatActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
    }

    @Override
    public void onProceedToAnalysisScreen(@NonNull Document document) {
        // Analysis was not done or the document was modified when the user pressed the Next button
        // Continue to the AnalysisFragment
        Intent intent = new Intent(this, CustomAnalysisAppCompatActivity.class);
        intent.putExtra(CustomAnalysisAppCompatActivity.EXTRA_IN_DOCUMENT, document);
        startActivity(intent);
    }

    @Override
    public void onDocumentReviewedAndAnalyzed(@NonNull Document document) {
        // Document was analyzed, we should have the extractions and can finish here (no need to continue to the AnalysisFragment)
        Toast.makeText(this, "Photo extractions received", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {

    }
}
