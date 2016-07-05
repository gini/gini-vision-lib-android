package net.gini.android.vision.onboarding;

import android.content.Context;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

class OnboardingFragmentImpl {

    private static final OnboardingFragmentListener NO_OP_LISTENER = new OnboardingFragmentListener() {
        @Override
        public void onCloseOnboarding() {
        }

        @Override
        public void onError(GiniVisionError error) {
        }
    };

    private final OnboardingFragmentImplCallback mFragment;
    private OnboardingFragmentListener mListener = NO_OP_LISTENER;
    private final ArrayList<OnboardingPage> mPages;

    private ViewPager mViewPager;
    private LinearLayout mLayoutPageIndicators;
    private ImageButton mButtonNext;

    PageChangeListener mPageChangeListener;

    public OnboardingFragmentImpl(OnboardingFragmentImplCallback fragment) {
        mFragment = fragment;
        mPages = DefaultPages.asArrayList();
    }

    public OnboardingFragmentImpl(OnboardingFragmentImplCallback fragment, ArrayList<OnboardingPage> pages) {
        mFragment = fragment;
        mPages = pages != null ? pages : DefaultPages.asArrayList();
        mPages.add(new OnboardingPage(0, 0));
    }

    public void setListener(OnboardingFragmentListener listener) {
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
        mViewPager = (ViewPager) view.findViewById(R.id.gv_viewpager);
        mLayoutPageIndicators = (LinearLayout) view.findViewById(R.id.gv_layout_page_indicators);
        mButtonNext = (ImageButton) view.findViewById(R.id.gv_button_next);
    }

    private void setUpViewPager() {
        mViewPager.setAdapter(mFragment.getViewPagerAdapter(mPages));

        PageIndicators pageIndicators = new PageIndicators(mFragment.getActivity(), mPages.size() - 1, mLayoutPageIndicators);
        pageIndicators.create();

        mPageChangeListener = new PageChangeListener(pageIndicators, 0, mPages.size(), new PageChangeListener.Callback() {
            @Override
            public void onLastPage() {
                mListener.onCloseOnboarding();
            }
        });
        mPageChangeListener.init();

        mViewPager.addOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(0);
    }

    private void setInputHandlers() {
        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextPage();
            }
        });
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
        private boolean mOnLastPage;

        PageChangeListener(PageIndicators pageIndicators, int currentPage, int pages, Callback callback) {
            mPageIndicators = pageIndicators;
            mCurrentPage = currentPage;
            mPages = pages;
            mCallback = callback;
        }

        public void init() {
            mPageIndicators.setActive(0);
        }

        public int getCurrentPage() {
            return mCurrentPage;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mPageIndicators.setActive(position);
            mCurrentPage = position;
            mOnLastPage = position == mPages - 1;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnLastPage && state == ViewPager.SCROLL_STATE_IDLE) {
                mCallback.onLastPage();
            }
        }


    }
}
