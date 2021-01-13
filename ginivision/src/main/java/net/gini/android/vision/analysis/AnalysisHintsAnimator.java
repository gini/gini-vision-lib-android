package net.gini.android.vision.analysis;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.gini.android.vision.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.ViewPropertyAnimatorCompat;
import androidx.core.view.ViewPropertyAnimatorListenerAdapter;

/**
 * Created by Alpar Szotyori on 09.05.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 *
 */
class AnalysisHintsAnimator {

    private static final int HINT_START_DELAY = 5000;
    private static final int HINT_ANIMATION_DURATION = 500;
    private static final int HINT_CYCLE_INTERVAL = 4000;

    private final Context mContext;
    private final View mHintContainer;
    private final ImageView mHintImageView;
    private final TextView mHintTextView;
    private final TextView mHintHeadlineTextView;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private int mContainerViewHeight;
    private ViewPropertyAnimatorCompat mHintHeadlineAnimation;
    private ViewPropertyAnimatorCompat mHintAnimation;
    private List<AnalysisHint> mHints;
    private Runnable mHintStartRunnable;
    private Runnable mHintCycleRunnable;

    public AnalysisHintsAnimator(
            @NonNull final Context context,
            @NonNull final View hintContainer,
            @NonNull final ImageView hintImageView,
            @NonNull final TextView hintTextView,
            @NonNull final TextView hintHeadlineTextView) {
        mContext = context;
        mHintContainer = hintContainer;
        mHintImageView = hintImageView;
        mHintTextView = hintTextView;
        mHintHeadlineTextView = hintHeadlineTextView;
    }

    public void setContainerViewHeight(final int containerViewHeight) {
        mContainerViewHeight = containerViewHeight;
    }

    public void start() {
        mHintCycleRunnable = new Runnable() {
            @Override
            public void run() {
                mHintAnimation = getSlideDownAnimation();
                mHintAnimation.start();
            }
        };
        mHintStartRunnable = new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(mHintHeadlineTextView.getText())) {
                    mHintHeadlineAnimation = getHintHeadlineSlideDownAnimation();
                    mHintHeadlineAnimation.start();
                }
                mHandler.post(mHintCycleRunnable);
            }
        };
        mHandler.postDelayed(mHintStartRunnable, HINT_START_DELAY);
    }

    private ViewPropertyAnimatorCompat getHintHeadlineSlideDownAnimation() {
        return ViewCompat.animate(mHintHeadlineTextView)
                .translationY(mContainerViewHeight)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        startShowHeadlineAnimation();
                    }
                });
    }

    private void startShowHeadlineAnimation() {
        showHeadlineText();
        mHintHeadlineAnimation = getHintHeadlineSlideUpAnimation();
        mHintHeadlineAnimation.start();
    }

    private void showHeadlineText() {
        mHintHeadlineTextView.setText(R.string.gv_analysis_hint_headline);
    }

    private ViewPropertyAnimatorCompat getHintHeadlineSlideUpAnimation() {
        return ViewCompat.animate(mHintHeadlineTextView)
                .translationY(0)
                .setDuration(HINT_ANIMATION_DURATION);
    }

    @NonNull
    private ViewPropertyAnimatorCompat getSlideDownAnimation() {
        return ViewCompat.animate(mHintContainer)
                .translationY(mContainerViewHeight)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        startNextHintSlideUpAnimation();
                    }
                });
    }

    private void startNextHintSlideUpAnimation() {
        setNextHint();
        mHintAnimation = getSlideUpAnimation();
        mHintAnimation.start();
    }

    @NonNull
    private ViewPropertyAnimatorCompat getSlideUpAnimation() {
        return ViewCompat.animate(mHintContainer)
                .translationY(0)
                .setDuration(HINT_ANIMATION_DURATION)
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(final View view) {
                        mHandler.postDelayed(mHintCycleRunnable, HINT_CYCLE_INTERVAL);
                    }
                });
    }

    private void setNextHint() {
        final AnalysisHint nextHint = getNextHint();
        mHintImageView.setImageDrawable(
                ContextCompat.getDrawable(mContext, nextHint.getDrawableResource()));
        mHintTextView.setText(nextHint.getTextResource());
    }

    private AnalysisHint getNextHint() {
        final AnalysisHint analysisHint = mHints.remove(0);
        mHints.add(analysisHint);
        return analysisHint;
    }

    public void setHints(final List<AnalysisHint> hints) {
        mHints = hints;
    }

    public void stop() {
        mHandler.removeCallbacks(mHintStartRunnable);
        mHandler.removeCallbacks(mHintCycleRunnable);
        if (mHintAnimation != null) {
            mHintAnimation.cancel();
            mHintContainer.clearAnimation();
            mHintAnimation.setListener(null);
        }
        if (mHintHeadlineAnimation != null) {
            mHintHeadlineAnimation.cancel();
            mHintHeadlineTextView.clearAnimation();
            mHintHeadlineAnimation.setListener(null);
        }
    }
}
