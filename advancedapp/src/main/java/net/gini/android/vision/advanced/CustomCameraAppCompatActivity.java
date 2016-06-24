package net.gini.android.vision.advanced;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.camera.Document;
import net.gini.android.visionadvtest.R;

public class CustomCameraAppCompatActivity extends AppCompatActivity implements CameraFragmentListener {

    private GiniVisionCoordinator mGiniVisionCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_camera_compat);
        createGiniVisionCoordinator();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGiniVisionCoordinator.onScannerStarted();
    }

    @Override
    public void onDocumentAvailable(Document document) {
        Intent intent = new Intent(this, CustomReviewDocumentAppCompatActivity.class);
        startActivity(intent);
        createGiniVisionCoordinator();
    }

    private void createGiniVisionCoordinator() {
        mGiniVisionCoordinator = GiniVisionCoordinator.createInstance(this);
        mGiniVisionCoordinator
                .setListener(new GiniVisionCoordinator.Listener() {
                    @Override
                    public void onShowOnboarding() {
                        Intent intent = new Intent(CustomCameraAppCompatActivity.this, CustomOnboardingAppCompatActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onError(GiniVisionError error) {

    }
}
