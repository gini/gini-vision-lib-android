package net.gini.android.vision.component.camera.compat;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import net.gini.android.vision.component.review.multipage.MultiPageReviewExampleActivity;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.onboarding.OnboardingFragmentCompat;
import net.gini.android.vision.review.multipage.MultiPageReviewActivity;

/**
 * Created by Alpar Szotyori on 04.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Creates compatibility library fragments and activities for the Camera Screen.
 */
public class CameraScreenHandlerAppCompat extends BaseCameraScreenHandler {

    private static final int MULTI_PAGE_REVIEW_REQUEST = 101;

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
        actionBar.setTitle(R.string.camera_screen_title);
        actionBar.setSubtitle(mAppCompatActivity.getString(R.string.camera_screen_subtitle));
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
    protected CameraFragmentInterface createCameraFragment() {
        mCameraFragment = CameraFragmentCompat.createInstance();
        return mCameraFragment;
    }

    @Override
    protected void showCameraFragment() {
        mAppCompatActivity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.camera_container, mCameraFragment)
                .commit();
    }

    @Override
    protected CameraFragmentInterface retrieveCameraFragment() {
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

    @Override
    public void onProceedToMultiPageReviewScreen(
            @NonNull final GiniVisionMultiPageDocument multiPageDocument) {
        final Intent intent = MultiPageReviewExampleActivity.newInstance(multiPageDocument,
                mAppCompatActivity);
        mAppCompatActivity.startActivityForResult(intent, MULTI_PAGE_REVIEW_REQUEST);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REVIEW_REQUEST:
                if (resultCode == ReviewExampleAppCompatActivity.RESULT_ADD_MORE_PAGES) {
                    if (data != null) {
                        final Document document = data.getParcelableExtra(
                                ReviewExampleAppCompatActivity.EXTRA_OUT_MULTI_PAGE_FIRST_PAGE);
                        if (document != null) {
                            mCameraFragment.startMultiPage(document);
                        }
                    }
                }
                break;
            case MULTI_PAGE_REVIEW_REQUEST:
                if (resultCode == MultiPageReviewExampleActivity.RESULT_MULTI_PAGE_DOCUMENT) {
                    if (data != null) {
                        final GiniVisionMultiPageDocument multiPageDocument =
                                data.getParcelableExtra(
                                        MultiPageReviewActivity.EXTRA_OUT_DOCUMENT);
                        if (multiPageDocument != null) {
                            mCameraFragment.setMultiPageDocument(multiPageDocument);
                        }
                    }
                    break;
                }
        }
    }
}
