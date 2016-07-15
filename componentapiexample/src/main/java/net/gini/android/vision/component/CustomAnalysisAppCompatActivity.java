package net.gini.android.vision.component;

import static net.gini.android.vision.component.Util.readAsset;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisFragmentCompat;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.visionadvtest.R;

public class CustomAnalysisAppCompatActivity extends AppCompatActivity implements AnalysisFragmentListener {

    public static final String EXTRA_IN_DOCUMENT = "EXTRA_IN_DOCUMENT";

    private AnalysisFragmentCompat mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_analysis_compat);
        // The AnalysisFragment cannot be directly added to a layout, because it requires a Document
        createFragment();
        showFragment();
    }

    private void createFragment() {
        Document document = getIntent().getParcelableExtra(EXTRA_IN_DOCUMENT);
        if (document == null) {
            document = Document.fromPhoto(Photo.fromJpeg(readAsset(this, "test_document.jpg"), 0));
        }
        mFragment = AnalysisFragmentCompat.createInstance(document);
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_analyze_document, mFragment)
                .commit();
    }

    @Override
    public void onAnalyzeDocument(@NonNull Document document) {
        // We can start analyzing the document by sending it to the Gini API
        // Currently we only simulate analysis and show an error after 2500 ms to view the error customizations
        // and when the user presses the button on the error snackbar, we tell the AnalysisFragment that the
        // document was analyzed and we finish here
        mFragment.startScanAnimation();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFragment.stopScanAnimation();
                mFragment.showError("Something went wrong", "Fix it", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mFragment.onDocumentAnalyzed();
                        Toast.makeText(CustomAnalysisAppCompatActivity.this, "Photo was analyzed", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            }
        }, 2500);
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {

    }
}
