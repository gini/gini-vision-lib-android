package net.gini.android.vision.component.camera;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraFragmentCompat;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.review.ReviewExampleAppCompatActivity;
import net.gini.android.vision.onboarding.OnboardingFragmentCompat;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraScreenHandlerAppCompat extends AbstractCameraScreenHandler {

    private final AppCompatActivity mAppCompatActivity;
    private CameraFragmentCompat mCameraFragment;

    CameraScreenHandlerAppCompat(final Activity activity) {
        super(activity);
        mAppCompatActivity = (AppCompatActivity) activity;
    }

    @Override
    protected void removeOnboardingFragment() {
        mCameraFragment.showInterface();
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
    protected void showCameraFragment() {
        mCameraFragment = CameraFragmentCompat.createInstance(
                getGiniVisionFeatureConfiguration());
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_container, mCameraFragment)
                .commit();
    }

    @Override
    protected void retainCameraFragment() {
        mCameraFragment =
                (CameraFragmentCompat) mAppCompatActivity.getSupportFragmentManager().findFragmentById(
                        R.id.camera_container);
    }

    @Override
    protected void showOnboardingFragment() {
        mCameraFragment.hideInterface();
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
