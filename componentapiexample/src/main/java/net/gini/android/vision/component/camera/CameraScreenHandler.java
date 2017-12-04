package net.gini.android.vision.component.camera;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraFragmentStandard;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.AnalysisExampleActivity;
import net.gini.android.vision.component.review.ReviewExampleActivity;
import net.gini.android.vision.onboarding.OnboardingFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraScreenHandler extends AbstractCameraScreenHandler {

    private CameraFragmentStandard mCameraFragment;

    CameraScreenHandler(final Activity activity) {
        super(activity);
    }

    @Override
    protected void removeOnboardingFragment() {
        mCameraFragment.showInterface();
        final Fragment fragment = getActivity().getFragmentManager().findFragmentById(
                R.id.onboarding_container);
        if (fragment != null) {
            getActivity().getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                    .remove(fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    protected void setTitlesForCamera() {
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("Seite abfotografieren");
        actionBar.setSubtitle("Vollständig in den Rahmen einpassen");
    }

    @Override
    protected Intent getReviewActivityIntent(final Document document) {
        return ReviewExampleActivity.newInstance(document, getActivity());
    }

    @Override
    protected Intent getAnalysisActivityIntent(final Document document) {
        return AnalysisExampleActivity.newInstance(document, null, getActivity());
    }

    @Override
    protected boolean isOnboardingVisible() {
        return getActivity().getFragmentManager().findFragmentById(
                R.id.onboarding_container) != null;
    }

    @Override
    protected void setUpActionBar() {

    }

    @Override
    protected void showCameraFragment() {
        mCameraFragment = CameraFragmentStandard.createInstance(
                getGiniVisionFeatureConfiguration());
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.camera_container, mCameraFragment)
                .commit();
    }

    @Override
    protected void retainCameraFragment() {
        mCameraFragment =
                (CameraFragmentStandard) getActivity().getFragmentManager().findFragmentById(
                        R.id.camera_container);
    }

    @Override
    protected void showOnboardingFragment() {
        mCameraFragment.hideInterface();
        getActivity().getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out)
                .replace(R.id.onboarding_container, new OnboardingFragmentStandard())
                .commit();
    }

    @Override
    protected void setTitlesForOnboarding() {
        final ActionBar actionBar = getActivity().getActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setTitle("");
        actionBar.setSubtitle("");
    }
}
