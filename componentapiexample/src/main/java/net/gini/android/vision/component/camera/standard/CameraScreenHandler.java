package net.gini.android.vision.component.camera.standard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraFragmentInterface;
import net.gini.android.vision.camera.CameraFragmentStandard;
import net.gini.android.vision.component.R;
import net.gini.android.vision.component.analysis.standard.AnalysisExampleActivity;
import net.gini.android.vision.component.camera.BaseCameraScreenHandler;
import net.gini.android.vision.component.review.standard.ReviewExampleActivity;
import net.gini.android.vision.onboarding.OnboardingFragmentStandard;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Creates standard fragments and activities for the Camera Screen.
 */
public class CameraScreenHandler extends BaseCameraScreenHandler {

    private CameraFragmentStandard mCameraFragment;

    CameraScreenHandler(final Activity activity) {
        super(activity);
    }

    @Override
    protected void removeOnboardingFragment() {
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
        actionBar.setTitle(R.string.camera_screen_title);
        actionBar.setSubtitle(getActivity().getString(R.string.camera_screen_subtitle));
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
    protected CameraFragmentInterface createCameraFragment() {
        mCameraFragment = CameraFragmentStandard.createInstance();
        return mCameraFragment;
    }

    @Override
    protected void showCameraFragment() {
        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.camera_container, mCameraFragment)
                .commit();
    }

    @Override
    protected CameraFragmentInterface retrieveCameraFragment() {
        mCameraFragment =
                (CameraFragmentStandard) getActivity().getFragmentManager().findFragmentById(
                        R.id.camera_container);
        return mCameraFragment;
    }

    @Override
    protected void showOnboardingFragment() {
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
