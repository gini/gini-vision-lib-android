package net.gini.android.vision.analyse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.scanner.Document;

public abstract class AnalyseDocumentActivity extends AppCompatActivity implements AnalyseDocumentFragmentListener, AnalyseDocumentFragmentInterface {

    public static final String EXTRA_IN_DOCUMENT = "GV_EXTRA_IN_DOCUMENT";
    public static final String EXTRA_OUT_ERROR = "GV_EXTRA_OUT_ERROR";

    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    AnalyseDocumentFragmentCompat mFragment;
    private Document mDocument;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_analyse_document);
        if (savedInstanceState == null) {
            readExtras();
            createFragment();
            showFragment();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDocument = null;
    }

    private void readExtras() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mDocument = extras.getParcelable(EXTRA_IN_DOCUMENT);
        }
        checkRequiredExtras();
    }

    private void checkRequiredExtras() {
        if (mDocument == null) {
            throw new IllegalStateException("AnalyseDocumentActivity requires a Document. Set it as an extra using the EXTRA_IN_DOCUMENT key.");
        }
    }

    private void createFragment() {
        mFragment = AnalyseDocumentFragmentCompat.createInstance(mDocument);
    }

    private void showFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.gv_fragment_analyse_document, mFragment)
                .commit();
    }

    @Override
    public abstract void onAnalyzeDocument(Document document);

    @Override
    public void onError(GiniVisionError error) {
        Intent result = new Intent();
        result.putExtra(EXTRA_OUT_ERROR, error);
        setResult(RESULT_ERROR, result);
        finish();
    }

    @Override
    public void startScanAnimation() {
        mFragment.startScanAnimation();
    }

    @Override
    public void stopScanAnimation() {
        mFragment.stopScanAnimation();
    }

    @Override
    public void onDocumentAnalyzed() {
        mFragment.onDocumentAnalyzed();
        Intent result = new Intent();
        onAddDataToResult(result);
        setResult(RESULT_OK, result);
        finish();
    }

    protected abstract void onAddDataToResult(Intent result);
}
