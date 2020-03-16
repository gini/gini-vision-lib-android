package net.gini.android.vision.onboarding;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.AnimatorListenerNoOp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.viewpager.widget.ViewPager;
import jersey.repackaged.jsr166e.CompletableFuture;

class OnboardingFragmentImpl extends OnboardingScreenContract.View {

    private static final Logger LOG = LoggerFactory.getLogger(OnboardingFragmentImpl.class);


    private final OnboardingFragmentImplCallback mFragment;
    @VisibleForTesting
    ImageButton mButtonNext;
    private ViewPager mViewPager;
    private LinearLayout mLayoutPageIndicators;
    private PageIndicators mPageIndicators;

    public OnboardingFragmentImpl(final OnboardingFragmentImplCallback fragment,
            final boolean showEmptyLastPage) {
        this(fragment, showEmptyLastPage, null);
    }

    public OnboardingFragmentImpl(final OnboardingFragmentImplCallback fragment,
            final boolean showEmptyLastPage, final ArrayList<OnboardingPage> pages) { // NOPMD
        mFragment = fragment;
        if (mFragment.getActivity() == null) {
            throw new IllegalStateException("Missing activity for fragment.");
        }
        initPresenter(mFragment.getActivity(), pages, showEmptyLastPage);
    }

    private void initPresenter(@NonNull final Activity activity,
            @Nullable final ArrayList<OnboardingPage> pages, // NOPMD - Bundle
            final boolean showEmptyLastPage) {
        createPresenter(activity);
        if (showEmptyLastPage) {
            getPresenter().addEmptyLastPage();
        }
        if (pages != null) {
            getPresenter().setCustomPages(pages);
        }
    }

    @VisibleForTesting
    void createPresenter(@NonNull final Activity activity) {
        new OnboardingScreenPresenter(activity, this);
    }

    @Override
    void showPages(@NonNull final List<OnboardingPage> pages,
            final boolean showEmptyLastPage) {
        setUpViewPager(pages, showEmptyLastPage);
    }

    @Override
    void scrollToPage(final int pageIndex) {
        mViewPager.setCurrentItem(pageIndex);
    }

    @Override
    void activatePageIndicatorForPage(final int pageIndex) {
        mPageIndicators.setActive(pageIndex);
    }

    @Override
    CompletableFuture<Void> slideOutViews() {
        final CompletableFuture<Void> future = new CompletableFuture<>();
        // If width is still 0  set it to a big value to make sure the view
        // will slide out completely
        int layoutPageIndicatorsWidth = mLayoutPageIndicators.getWidth();
        layoutPageIndicatorsWidth =
                layoutPageIndicatorsWidth != 0 ? layoutPageIndicatorsWidth : 10000;
        int buttonNextWidth = mButtonNext.getWidth();
        buttonNextWidth = buttonNextWidth != 0 ? buttonNextWidth : 10000;

        mLayoutPageIndicators.animate()
                .setDuration(150)
                .translationX(-10 * layoutPageIndicatorsWidth);
        mButtonNext.animate()
                .setDuration(150)
                .translationX(2 * buttonNextWidth)
                .setListener(new AnimatorListenerNoOp() {
                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        future.complete(null);
                    }
                });
        return future;
    }

    private void setUpViewPager(
            @NonNull final List<OnboardingPage> pages,
            final boolean showEmptyLastPage) {
        mViewPager.setAdapter(mFragment.getViewPagerAdapter(pages));

        final int numberOfPageIndicators = showEmptyLastPage ? pages.size() - 1 : pages.size();
        mPageIndicators = new PageIndicators(mFragment.getActivity(),
                numberOfPageIndicators, mLayoutPageIndicators);
        mPageIndicators.create();

        mViewPager.addOnPageChangeListener(new PageChangeListener(getPresenter()));
    }

    @Override
    public void setListener(@NonNull final OnboardingFragmentListener listener) {
        getPresenter().setListener(listener);
    }

    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.gv_fragment_onboarding, container, false);
        bindViews(view);
        setInputHandlers();
        getPresenter().start();
        return view;
    }

    private void bindViews(final View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.gv_onboarding_viewpager);
        mLayoutPageIndicators = (LinearLayout) view.findViewById(R.id.gv_layout_page_indicators);
        mButtonNext = (ImageButton) view.findViewById(R.id.gv_button_next);
    }

    private void setInputHandlers() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                getPresenter().showNextPage();
            }
        });
    }

    @VisibleForTesting
    PageIndicators getPageIndicators() {
        return mPageIndicators;
    }

    @VisibleForTesting
    static class PageIndicators {

        private final Context mContext;
        private final int mNrOfPages;
        private final LinearLayout mLayoutPageIndicators;
        private final List<ImageView> mPageIndicators = new ArrayList<>();

        PageIndicators(final Context context, final int nrOfPages,
                final LinearLayout layoutPageIndicators) {
            mContext = context;
            mNrOfPages = nrOfPages;
            mLayoutPageIndicators = layoutPageIndicators;
        }

        public void create() {
            createPageIndicators(mNrOfPages);
            for (int i = 0; i < mPageIndicators.size(); i++) {
                final ImageView pageIndicator = mPageIndicators.get(i);
                mLayoutPageIndicators.addView(pageIndicator);
                if (i < mPageIndicators.size() - 1) {
                    mLayoutPageIndicators.addView(createSpace());
                }
            }
        }

        private void createPageIndicators(final int nrOfPages) {
            for (int i = 0; i < nrOfPages; i++) {
                mPageIndicators.add(createPageIndicator());
            }
        }

        private ImageView createPageIndicator() {
            final ImageView pageIndicator = new ImageView(mContext);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    mContext.getResources().getDimensionPixelSize(
                            R.dimen.gv_onboarding_indicator_width),
                    mContext.getResources().getDimensionPixelSize(
                            R.dimen.gv_onboarding_indicator_height));
            pageIndicator.setLayoutParams(layoutParams);
            pageIndicator.setScaleType(ImageView.ScaleType.CENTER);
            pageIndicator.setImageDrawable(mContext.getResources().getDrawable(
                    R.drawable.gv_onboarding_indicator_inactive));
            pageIndicator.setTag("pageIndicator");
            pageIndicator.setContentDescription("inactive");
            return pageIndicator;
        }

        private Space createSpace() {
            final Space space = new Space(mContext);
            final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    mContext.getResources().getDimensionPixelSize(
                            R.dimen.gv_onboarding_indicator_width),
                    mContext.getResources().getDimensionPixelSize(
                            R.dimen.gv_onboarding_indicator_height));
            space.setLayoutParams(layoutParams);
            return space;
        }

        public void setActive(final int page) {
            if (page >= mPageIndicators.size()) {
                return;
            }
            deactivatePageIndicators();
            final ImageView pageIndicator = mPageIndicators.get(page);
            pageIndicator.setImageDrawable(
                    mContext.getResources().getDrawable(R.drawable.gv_onboarding_indicator_active));
            pageIndicator.setContentDescription("active");
        }

        private void deactivatePageIndicators() {
            for (final ImageView pageIndicator : mPageIndicators) {
                pageIndicator.setImageDrawable(mContext.getResources().getDrawable(
                        R.drawable.gv_onboarding_indicator_inactive));
                pageIndicator.setContentDescription("inactive");
            }
        }

        @VisibleForTesting
        List<ImageView> getPageIndicatorImageViews() {
            return mPageIndicators;
        }
    }

    @VisibleForTesting
    static class PageChangeListener implements ViewPager.OnPageChangeListener {

        private final OnboardingScreenContract.Presenter mPresenter;

        PageChangeListener(@NonNull final OnboardingScreenContract.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset,
                final int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(final int position) {
            LOG.info("page selected: {}", position);
            mPresenter.onScrolledToPage(position);
        }

        @Override
        public void onPageScrollStateChanged(final int state) {
        }
    }
}
