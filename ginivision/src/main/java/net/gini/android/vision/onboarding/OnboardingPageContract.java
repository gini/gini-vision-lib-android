package net.gini.android.vision.onboarding;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.GiniVisionBasePresenter;
import net.gini.android.vision.GiniVisionBaseView;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface OnboardingPageContract {

    abstract class View extends GiniVisionBaseView<Presenter> {

        abstract void showImage(@Nullable final Drawable image);

        abstract void showText(@Nullable final String text);

        abstract void showTransparentBackground();
    }

    abstract class Presenter extends GiniVisionBasePresenter<View> {

        Presenter(
                @NonNull final Application app,
                @NonNull final View view) {
            super(app, view);
        }

        abstract void setPage(@NonNull final OnboardingPage page);
    }

}
