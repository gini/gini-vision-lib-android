package net.gini.android.vision.onboarding;

import android.app.Activity;

import net.gini.android.vision.GiniVisionBasePresenter;
import net.gini.android.vision.GiniVisionBaseView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface OnboardingPageContract {

    abstract class View implements GiniVisionBaseView<Presenter> {

        private Presenter mPresenter;

        @Override
        public void setPresenter(@NonNull final Presenter presenter) {
            mPresenter = presenter;
        }

        public Presenter getPresenter() {
            return mPresenter;
        }

        abstract void showImage(@DrawableRes final int imageResId, final boolean rotated);

        abstract void showText(@StringRes final int textResId);

        abstract void showTransparentBackground();
    }

    abstract class Presenter extends GiniVisionBasePresenter<View> {

        Presenter(
                @NonNull final Activity activity,
                @NonNull final View view) {
            super(activity, view);
        }

        abstract void setPage(@NonNull final OnboardingPage page);
    }

}
