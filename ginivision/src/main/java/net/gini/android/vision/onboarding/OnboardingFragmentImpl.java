package net.gini.android.vision.onboarding;

import android.animation.Animator;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Space;

import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.R;
import net.gini.android.vision.internal.ui.AnimatorListenerNoOp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

class OnboardingFragmentImpl {

    private static final Logger LOG = LoggerFactory.getLogger(OnboardingFragmentImpl.class);

    private static final OnboardingFragmentListener NO_OP_LISTENER = new OnboardingFragmentListener() {
        @Override
        public void onCloseOnboarding() {
        }

        @Override
        public void onError(@NonNull GiniVisionError error) {
        }
    };

    private final OnboardingFragmentImplCallback mFragment;
    private OnboardingFragmentListener mListener = NO_OP_LISTENER;
    private final ArrayList<OnboardingPage> mPages;
    private final boolean mShowEmptyLastPage;

    private ViewPager mViewPager;
    private LinearLayout mLayoutPageIndicators;
    private ImageButton mButtonNext;

    private PageChangeListener mPageChangeListener;

    public OnboardingFragmentImpl(OnboardingFragmentImplCallback fragment, boolean showEmptyLastPage) {
        mFragment = fragment;
        mPages = DefaultPages.asArrayList();
        mShowEmptyLastPage = showEmptyLastPage;
        if (mShowEmptyLastPage) {
            addTransparentPage();
        }
    }

    public OnboardingFragmentImpl(OnboardingFragmentImplCallback fragment, boolean showEmptyLastPage, ArrayList<OnboardingPage> pages) {
        mFragment = fragment;
        mPages = pages != null ? new ArrayList<>(pages) : DefaultPages.asArrayList();
        mShowEmptyLastPage = showEmptyLastPage;
        if (mShowEmptyLastPage) {
            addTransparentPage();
        }
    }

    private void addTransparentPage() {
        LOG.info("appended an empty transparent page");
        mPages.add(new OnboardingPage(0, 0, true));
    }

    public void setListener(@Nullable OnboardingFragmentListener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_onboarding, container, false);
        bindViews(view);
        setUpViewPager();
        setInputHandlers();
        return view;
    }

    private void bindViews(View view) {
        mViewPager = (ViewPager) view.findViewById(R.id.gv_onboarding_viewpager);
        mLayoutPageIndicators = (LinearLayout) view.findViewById(R.id.gv_layout_page_indicators);
        mButtonNext = (ImageButton) view.findViewById(R.id.gv_button_next);
    }

    private void setUpViewPager() {
        mViewPager.setAdapter(mFragment.getViewPagerAdapter(mPages));

        int nrOfPageIndicators = mShowEmptyLastPage ? mPages.size() - 1 : mPages.size();
        PageIndicators pageIndicators = new PageIndicators(mFragment.getActivity(), nrOfPageIndicators, mLayoutPageIndicators);
        pageIndicators.create();

        mPageChangeListener = new PageChangeListener(pageIndicators, 0, mPages.size(), new PageChangeListener.Callback() {
            @Override
            public void onLastPage() {
                // Only when an empty last page is shown slide out the page indicator and next button and notify
                // the listener that the onboarding should be closed
                if (mShowEmptyLastPage) {
                    slideOutViewsAndNotifyListener();
                }
            }
        });
        mPageChangeListener.init();

        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(0);
    }

    private void slideOutViewsAndNotifyListener() {
        // If width is still 0  set it to a big value to make sure the view
        // will slide out completely
        int layoutPageIndicatorsWidth = mLayoutPageIndicators.getWidth();
        layoutPageIndicatorsWidth = layoutPageIndicatorsWidth != 0 ? layoutPageIndicatorsWidth : 10000;
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
                    public void onAnimationEnd(Animator animation) {
                        mListener.onCloseOnboarding();
                    }
                });
    }

    private void setInputHandlers() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnLastPage()) {
                    mListener.onCloseOnboarding();
                } else {
                    showNextPage();
                }
            }
        });
    }

    private boolean isOnLastPage() {
        return mPageChangeListener.getCurrentPage() == mPages.size() - 1;
    }

    private void showNextPage() {
        int nextPage = mPageChangeListener.getCurrentPage() + 1;
        if (nextPage < mPages.size()) {
            mViewPager.setCurrentItem(nextPage);
        }
    }

    @VisibleForTesting
    static class PageIndicators {

        private final Context mContext;
        private final int mNrOfPages;
        private final LinearLayout mLayoutPageIndicators;
        private List<ImageView> mPageIndicators = new ArrayList<>();

        PageIndicators(Context context, int nrOfPages, LinearLayout layoutPageIndicators) {
            mContext = context;
            mNrOfPages = nrOfPages;
            mLayoutPageIndicators = layoutPageIndicators;
        }

        public void create() {
            createPageIndicators(mNrOfPages);
            for (int i = 0; i < mPageIndicators.size(); i++) {
                ImageView pageIndicator = mPageIndicators.get(i);
                mLayoutPageIndicators.addView(pageIndicator);
                if (i < mPageIndicators.size() - 1) {
                    mLayoutPageIndicators.addView(createSpace());
                }
            }
        }

        public void setActive(int page) {
            if (page >= mPageIndicators.size()) {
                return;
            }
            deactivatePageIndicators();
            ImageView pageIndicator = mPageIndicators.get(page);
            pageIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gv_onboarding_indicator_active));
        }

        private void deactivatePageIndicators() {
            for (ImageView pageIndicator : mPageIndicators) {
                pageIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gv_onboarding_indicator_inactive));
            }
        }

        private void createPageIndicators(int nrOfPages) {
            for (int i = 0; i < nrOfPages; i++) {
                mPageIndicators.add(createPageIndicator());
            }
        }

        private ImageView createPageIndicator() {
            ImageView pageIndicator = new ImageView(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    mContext.getResources().getDimensionPixelSize(R.dimen.gv_onboarding_indicator_width),
                    mContext.getResources().getDimensionPixelSize(R.dimen.gv_onboarding_indicator_height));
            pageIndicator.setLayoutParams(layoutParams);
            pageIndicator.setScaleType(ImageView.ScaleType.CENTER);
            pageIndicator.setImageDrawable(mContext.getResources().getDrawable(R.drawable.gv_onboarding_indicator_inactive));
            return pageIndicator;
        }

        private Space createSpace() {
            Space space = new Space(mContext);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    mContext.getResources().getDimensionPixelSize(R.dimen.gv_onboarding_indicator_width),
                    mContext.getResources().getDimensionPixelSize(R.dimen.gv_onboarding_indicator_height));
            space.setLayoutParams(layoutParams);
            return space;
        }

        @VisibleForTesting
        LinearLayout getLayoutPageIndicators() {
            return mLayoutPageIndicators;
        }

        @VisibleForTesting
        List<ImageView> getPageIndicators() {
            return mPageIndicators;
        }
    }

    @VisibleForTesting
    static class PageChangeListener implements ViewPager.OnPageChangeListener {

        interface Callback {
            void onLastPage();
        }

        private final PageIndicators mPageIndicators;
        private final int mPages;
        private final Callback mCallback;
        private int mCurrentPage;

        PageChangeListener(PageIndicators pageIndicators, int currentPage, int pages, Callback callback) {
            mPageIndicators = pageIndicators;
            mCurrentPage = currentPage;
            mPages = pages;
            mCallback = callback;
        }

        public void init() {
            mPageIndicators.setActive(mCurrentPage);
        }

        public int getCurrentPage() {
            return mCurrentPage;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            LOG.info("page selected: {}", position);
            mPageIndicators.setActive(position);
            mCurrentPage = position;
            if (position == mPages - 1) {
                LOG.info("on last page: {}", position);
                mCallback.onLastPage();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }


    }
}
