package net.gini.android.vision.component;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionCoordinator;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.visionadvtest.R;

public class CustomCameraActivity extends Activity implements CameraFragmentListener {

    private GiniVisionCoordinator mGiniVisionCoordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // The CameraFragment is included into the layout
        setContentView(R.layout.activity_custom_camera);
        // The GiniVisionCoordinator helps in using the Gini Vision Library's default behaviour with the Component API
        createGiniVisionCoordinator();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGiniVisionCoordinator.onCameraStarted();
    }

    private void createGiniVisionCoordinator() {
        mGiniVisionCoordinator = GiniVisionCoordinator.createInstance(this);
        mGiniVisionCoordinator
                .setListener(new GiniVisionCoordinator.Listener() {
                    @Override
                    public void onShowOnboarding() {
                        Intent intent = new Intent(CustomCameraActivity.this, CustomOnboardingActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void onDocumentAvailable(@NonNull Document document) {
        // A photo was taken and we can continue to the ReviewFragment
        Intent intent = new Intent(this, CustomReviewActivity.class);
        intent.putExtra(CustomReviewActivity.EXTRA_IN_DOCUMENT, document);
        startActivity(intent);
    }

    @Override
    public void onError(@NonNull GiniVisionError error) {

    }
}
