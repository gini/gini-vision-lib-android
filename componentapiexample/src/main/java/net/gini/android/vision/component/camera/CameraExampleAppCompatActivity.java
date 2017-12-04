package net.gini.android.vision.component.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.component.R;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;

public class CameraExampleAppCompatActivity extends AppCompatActivity implements
        CameraFragmentListener,
        OnboardingFragmentListener {

    private CameraScreenHandlerAppCompat mCameraScreen;

    @Override
    public void onBackPressed() {
        if (mCameraScreen.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        mCameraScreen.onNewIntent(intent);
    }

    @Override
    public void onCloseOnboarding() {
        mCameraScreen.onCloseOnboarding();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_compat);
        mCameraScreen = new CameraScreenHandlerAppCompat(this);
        mCameraScreen.onCreate(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return mCameraScreen.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mCameraScreen.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDocumentAvailable(@NonNull final Document document) {
        mCameraScreen.onDocumentAvailable(document);
    }

    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {
        mCameraScreen.onCheckImportedDocument(document, callback);
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        mCameraScreen.onError(error);
    }
}
