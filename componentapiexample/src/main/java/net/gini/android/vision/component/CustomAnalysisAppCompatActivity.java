package net.gini.android.vision.component;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.analysis.AnalysisFragmentListener;
import net.gini.android.vision.analysis.AnalysisFragmentStandard;
import net.gini.android.vision.Document;
import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.visionadvtest.R;

public class CustomAnalysisAppCompatActivity extends AppCompatActivity implements AnalysisFragmentListener {

    AnalysisFragmentStandard mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_analysis_compat);
        createFragment();
        showFragment();
    }

    private void createFragment() {
        mFragment = AnalysisFragmentStandard.createInstance(Document.fromPhoto(Photo.fromJpeg(new byte[]{}, 0)));
    }

    private void showFragment() {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_analyze_document, mFragment)
                .commit();
    }

    @Override
    public void onAnalyzeDocument(@NonNull Document document) {
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
                    }
                });
            }
        }, 2500);
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {

    }
}
