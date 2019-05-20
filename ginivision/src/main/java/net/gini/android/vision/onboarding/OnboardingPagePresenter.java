package net.gini.android.vision.onboarding;

import android.app.Application;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.util.ContextHelper;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class OnboardingPagePresenter extends OnboardingPageContract.Presenter {

    private OnboardingPage mPage;

    OnboardingPagePresenter(
            @NonNull final Application app,
            @NonNull final OnboardingPageContract.View view) {
        super(app, view);
        view.setPresenter(this);
    }

    @Override
    void setPage(@NonNull final OnboardingPage page) {
        mPage = page;
    }

    @Override
    public void start() {
        showImage();
        showText();
        if (mPage.isTransparent()) {
            getView().showTransparentBackground();
        }
    }

    @Override
    public void stop() {

    }

    private void showImage() {
        if (mPage.getImageResId() == 0) {
            return;
        }
        final boolean rotated = !ContextHelper.isPortraitOrientation(getApp())
                && mPage.shouldRotateImageForLandscape();
        getView().showImage(mPage.getImageResId(), rotated);
    }

    private void showText() {
        if (mPage.getTextResId() == 0) {
            return;
        }
        getView().showText(mPage.getTextResId());
    }
}
