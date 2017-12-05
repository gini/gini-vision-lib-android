package net.gini.android.vision.component.camera.standard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.camera.CameraFragmentStandard;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.standard.AnalysisExampleActivity;
import net.gini.android.vision.component.review.standard.ReviewExampleActivity;
import net.gini.android.vision.help.HelpActivity;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;
import net.gini.android.vision.onboarding.OnboardingFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Standard Activity using the {@link CameraScreenHandler} to host the
 * {@link CameraFragmentStandard} and the {@link OnboardingFragmentStandard} and to start the
 * {@link ReviewExampleActivity}, the {@link AnalysisExampleActivity} or the {@link HelpActivity}.
 */
public class CameraExampleActivity extends Activity implements CameraFragmentListener,
        OnboardingFragmentListener {

    private CameraScreenHandler mCameraScreenHandler;

    @Override
    public void onCloseOnboarding() {
        mCameraScreenHandler.onCloseOnboarding();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mCameraScreenHandler = new CameraScreenHandler(this);
        mCameraScreenHandler.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        mCameraScreenHandler.onNewIntent(intent);
    }

    @Override
    public void onBackPressed() {
        if (mCameraScreenHandler.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        return mCameraScreenHandler.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (mCameraScreenHandler.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCameraScreenHandler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDocumentAvailable(@NonNull final Document document) {
        mCameraScreenHandler.onDocumentAvailable(document);
    }

    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {
        mCameraScreenHandler.onCheckImportedDocument(document, callback);
    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {
        mCameraScreenHandler.onError(error);
    }
}
