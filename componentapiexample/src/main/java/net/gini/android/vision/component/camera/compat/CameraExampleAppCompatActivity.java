package net.gini.android.vision.component.camera.compat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.PaymentData;
import net.gini.android.vision.camera.CameraFragmentCompat;
import net.gini.android.vision.camera.CameraFragmentListener;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.compat.AnalysisExampleAppCompatActivity;
import net.gini.android.vision.component.review.compat.ReviewExampleAppCompatActivity;
import net.gini.android.vision.help.HelpActivity;
import net.gini.android.vision.onboarding.OnboardingFragmentCompat;
import net.gini.android.vision.onboarding.OnboardingFragmentListener;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * AppCompatActivity using the {@link CameraScreenHandlerAppCompat} to host the
 * {@link CameraFragmentCompat} and the {@link OnboardingFragmentCompat} and to start the
 * {@link ReviewExampleAppCompatActivity}, the {@link AnalysisExampleAppCompatActivity} or the {@link HelpActivity}.
 */
public class CameraExampleAppCompatActivity extends AppCompatActivity implements
        CameraFragmentListener,
        OnboardingFragmentListener {

    private CameraScreenHandlerAppCompat mCameraScreenHandler;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCameraScreenHandler.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (mCameraScreenHandler.onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        mCameraScreenHandler.onNewIntent(intent);
    }

    @Override
    public void onCloseOnboarding() {
        mCameraScreenHandler.onCloseOnboarding();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_compat);
        mCameraScreenHandler = new CameraScreenHandlerAppCompat(this);
        mCameraScreenHandler.onCreate(savedInstanceState);
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
    public void onDocumentAvailable(@NonNull final Document document) {
        mCameraScreenHandler.onDocumentAvailable(document);
    }

    @Override
    public void onPaymentDataAvailable(@NonNull final PaymentData paymentData) {
        mCameraScreenHandler.onPaymentDataAvailable(paymentData);
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
