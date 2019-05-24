package net.gini.android.vision.onboarding;

import static net.gini.android.vision.internal.util.ContextHelper.isTablet;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.GiniVisionError;

import java.util.List;

/**
 * Created by Alpar Szotyori on 20.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class OnboardingScreenPresenter extends OnboardingScreenContract.Presenter {

    private static final OnboardingFragmentListener NO_OP_LISTENER =
            new OnboardingFragmentListener() {
                @Override
                public void onCloseOnboarding() {
                }

                @Override
                public void onError(@NonNull final GiniVisionError error) {
                }
            };

    private OnboardingFragmentListener mListener = NO_OP_LISTENER;
    private List<OnboardingPage> mPages;
    private boolean mShowEmptyLastPage;
    private int mCurrentPageIndex;

    OnboardingScreenPresenter(@NonNull final Application app,
            @NonNull final OnboardingScreenContract.View view) {
        super(app, view);
        view.setPresenter(this);
        mPages = getDefaultPages();
    }

    private List<OnboardingPage> getDefaultPages() {
        if (isTablet(getApp())) {
            return DefaultPagesTablet.asArrayList();
        } else {
            return DefaultPagesPhone.asArrayList();
        }
    }

    @Override
    void setCustomPages(@NonNull final List<OnboardingPage> pages) {
        mPages = pages;
        if (mShowEmptyLastPage) {
            addTransparentPage();
        }
    }

    @Override
    void addEmptyLastPage() {
        mShowEmptyLastPage = true;
        if (mPages != null) {
            addTransparentPage();
        }
    }

    @Override
    void showNextPage() {
        if (isOnLastPage()) {
            mListener.onCloseOnboarding();
        } else {
            scrollToNextPage();
        }
    }

    private boolean isOnLastPage() {
        return mCurrentPageIndex == mPages.size() - 1;
    }

    private void scrollToNextPage() {
        final int nextPageIndex = mCurrentPageIndex + 1;
        if (nextPageIndex < mPages.size()) {
            getView().scrollToPage(nextPageIndex);
        }
    }

    @Override
    void onScrolledToPage(final int pageIndex) {
        mCurrentPageIndex = pageIndex;
        getView().activatePageIndicatorForPage(mCurrentPageIndex);
        // Only when an empty last page is shown slide out the page indicator and next button
        // and notify the listener that the onboarding should be closed
        if (isOnLastPage() && mShowEmptyLastPage) {
            getView().slideOutViews()
                    .thenRun(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onCloseOnboarding();
                        }
                    });
        }
    }

    private void addTransparentPage() {
        mPages.add(new OnboardingPage(0, 0, true));
    }

    @Override
    public void start() {
        getView().showPages(mPages, mShowEmptyLastPage);
        mCurrentPageIndex = 0;
        getView().scrollToPage(mCurrentPageIndex);
        getView().activatePageIndicatorForPage(mCurrentPageIndex);
    }

    @Override
    public void stop() {

    }

    @Override
    public void setListener(@NonNull final OnboardingFragmentListener listener) {
        mListener = listener;
    }

    @VisibleForTesting
    List<OnboardingPage> getPages() {
        return mPages;
    }
}
