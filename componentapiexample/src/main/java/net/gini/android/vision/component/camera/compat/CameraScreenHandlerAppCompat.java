package net.gini.android.vision.component.camera.compat;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraFragmentCompat;
import net.gini.android.vision.camera.CameraFragmentInterface;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.standard.AnalysisExampleActivity;
import net.gini.android.vision.component.camera.BaseCameraScreenHandler;
import net.gini.android.vision.component.review.compat.ReviewExampleAppCompatActivity;
import net.gini.android.vision.onboarding.OnboardingFragmentCompat;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraScreenHandlerAppCompat extends BaseCameraScreenHandler {

    private final AppCompatActivity mAppCompatActivity;
    private CameraFragmentCompat mCameraFragment;

    CameraScreenHandlerAppCompat(final Activity activity) {
        super(activity);
        mAppCompatActivity = (AppCompatActivity) activity;
    }

    @Override
    protected void removeOnboardingFragment() {
        final Fragment fragment = mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                R.id.onboarding_container);
        if (fragment != null) {
            mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .remove(fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    protected void setTitlesForCamera() {
        final ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("Seite abfotografieren");
        actionBar.setSubtitle("Vollständig in den Rahmen einpassen");
    }

    @Override
    protected Intent getReviewActivityIntent(final Document document) {
        return ReviewExampleAppCompatActivity.newInstance(document, mAppCompatActivity);
    }

    @Override
    protected Intent getAnalysisActivityIntent(final Document document) {
        return AnalysisExampleActivity.newInstance(document, null, mAppCompatActivity);
    }

    @Override
    protected boolean isOnboardingVisible() {
        return mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                R.id.onboarding_container) != null;
    }

    @Override
    protected void setUpActionBar() {
        mAppCompatActivity.setSupportActionBar(
                (Toolbar) mAppCompatActivity.findViewById(R.id.toolbar));
    }

    @Override
    protected CameraFragmentInterface showCameraFragment() {
        mCameraFragment = CameraFragmentCompat.createInstance(
                getGiniVisionFeatureConfiguration());
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_container, mCameraFragment)
                .commit();
        return mCameraFragment;
    }

    @Override
    protected CameraFragmentInterface retainCameraFragment() {
        mCameraFragment =
                (CameraFragmentCompat) mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                        R.id.camera_container);
        return mCameraFragment;
    }

    @Override
    protected void showOnboardingFragment() {
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.onboarding_container, new OnboardingFragmentCompat())
                .commit();
    }

    @Override
    protected void setTitlesForOnboarding() {
        final ActionBar actionBar = mAppCompatActivity.getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("");
        actionBar.setSubtitle("");
    }
}
