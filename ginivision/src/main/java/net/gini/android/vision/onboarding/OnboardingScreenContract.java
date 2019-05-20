package net.gini.android.vision.onboarding;

import android.app.Application;
import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionBasePresenter;
import net.gini.android.vision.GiniVisionBaseView;

import java.util.List;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface OnboardingScreenContract {

    abstract class View extends GiniVisionBaseView<Presenter> implements
            OnboardingFragmentInterface {

        abstract void showPages(@NonNull final List<OnboardingPage> pages,
                final boolean showEmptyLastPage);

        abstract void scrollToPage(final int pageIndex);

        abstract void activatePageIndicatorForPage(final int pageIndex);

        abstract CompletableFuture<Void> slideOutViews();
    }

    abstract class Presenter extends GiniVisionBasePresenter<View> implements
            OnboardingFragmentInterface {

        Presenter(
                @NonNull final Application app,
                @NonNull final View view) {
            super(app, view);
        }

        abstract void setCustomPages(@NonNull final List<OnboardingPage> pages);

        abstract void enableEmptyLastPage(final boolean isEnabled);

        abstract void showNextPage();

        abstract void onScrolledToPage(final int pageIndex);

    }
}
