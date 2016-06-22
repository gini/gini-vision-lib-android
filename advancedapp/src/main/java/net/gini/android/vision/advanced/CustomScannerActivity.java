package net.gini.android.vision.advanced;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.scanner.Document;
import net.gini.android.vision.scanner.ScannerFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomScannerActivity extends Activity implements ScannerFragmentListener {

    private GiniVisionCoordinator mGiniVisionCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);
        createGiniVisionCoordinator();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGiniVisionCoordinator.onScannerStarted();
    }

    private void createGiniVisionCoordinator() {
        mGiniVisionCoordinator = GiniVisionCoordinator.createInstance(this);
        mGiniVisionCoordinator
                .setListener(new GiniVisionCoordinator.Listener() {
                    @Override
                    public void onShowOnboarding() {
                        Intent intent = new Intent(CustomScannerActivity.this, CustomOnboardingActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onDocumentAvailable(Document document) {
        Intent intent = new Intent(this, CustomReviewDocumentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError(GiniVisionError error) {

    }
}
