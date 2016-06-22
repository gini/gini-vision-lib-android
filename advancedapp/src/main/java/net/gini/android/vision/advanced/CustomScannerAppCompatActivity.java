package net.gini.android.vision.advanced;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.scanner.ScannerFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomScannerAppCompatActivity extends AppCompatActivity implements ScannerFragmentListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner_compat);
    }

    @Override
    public void onDocumentAvailable(Document document) {
        Intent intent = new Intent(this, CustomReviewDocumentAppCompatActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError(GiniVisionError error) {

    }
}
