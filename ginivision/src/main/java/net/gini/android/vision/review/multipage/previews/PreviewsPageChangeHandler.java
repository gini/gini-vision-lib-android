package net.gini.android.vision.review.multipage.previews;

/**
 * Created by Alpar Szotyori on 08.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import androidx.viewpager.widget.ViewPager;

/**
 * Internal use only.
 *
 * @suppress
 */
public class PreviewsPageChangeHandler implements ViewPager.OnPageChangeListener {

    private final PreviewsPageChangeListener mListener;
    private int mLastPosition = -1;

    public PreviewsPageChangeHandler(
            final PreviewsPageChangeListener listener) {
        mListener = listener;
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset,
            final int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
        if (mLastPosition != position) {
            mLastPosition = position;
            mListener.onPageSelected(position);
        }
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
    }
}
