package net.gini.android.vision.review;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 19.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class RotatableImageViewContainer extends FrameLayout {

    private boolean isRotating;
    private ImageView mImageView;
    private int mRotationDegrees;

    public RotatableImageViewContainer(final Context context) {
        super(context);
        init(context); // NOPMD - ImageView intended to be creatable by subclasses
    }

    private void init(final Context context) {
        setClipChildren(false);

        mImageView = createImageView(context);
        final LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        mImageView.setLayoutParams(layoutParams);

        addView(mImageView);
    }

    @NonNull
    protected ImageView createImageView(final Context context) {
        return new ImageView(context);
    }

    public RotatableImageViewContainer(final Context context,
            @Nullable final AttributeSet attrs) {
        super(context, attrs);
        init(context); // NOPMD - ImageView intended to be creatable by subclasses
    }

    public RotatableImageViewContainer(final Context context, @Nullable final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context); // NOPMD - ImageView intended to be creatable by subclasses
    }

    public RotatableImageViewContainer(final Context context, @Nullable final AttributeSet attrs,
            final int defStyleAttr,
            final int defStyleRes) { // NOPMD
        super(context, attrs, defStyleAttr);
        init(context); // NOPMD - ImageView intended to be creatable by subclasses
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isRotating) {
            // Set ImageView to match parent after rotation in order for animations to resize
            // the ImageView as expected
            if (mRotationDegrees % 360 == 90 || mRotationDegrees % 360 == 270) {
                mImageView.measure(heightMeasureSpec, widthMeasureSpec);
            } else {
                mImageView.measure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void rotateImageViewBy(final int degrees, final boolean animated) {
        rotateImageView((int) (mImageView.getRotation() + degrees), animated);
    }

    public void rotateImageView(final int degrees, final boolean animated) {
        if (degrees == mImageView.getRotation() || isRotating) {
            return;
        }

        isRotating = true;
        mRotationDegrees = degrees;

        if (!animated) {
            mImageView.setRotation(degrees);
            isRotating = false;
            // Set ImageView size to match parent after rotation (in onMeasure())
            requestLayout();
        }

        final ValueAnimator widthAnimation;
        final ValueAnimator heightAnimation;
        if (degrees % 360 == 90 || degrees % 360 == 270) {
            widthAnimation = ValueAnimator.ofInt(mImageView.getWidth(),
                    getHeight());
            heightAnimation = ValueAnimator.ofInt(mImageView.getHeight(),
                    getWidth());
        } else {
            widthAnimation = ValueAnimator.ofInt(mImageView.getWidth(),
                    getWidth());
            heightAnimation = ValueAnimator.ofInt(mImageView.getHeight(),
                    getHeight());
        }

        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final int width = (int) valueAnimator.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
                layoutParams.width = width;
                mImageView.requestLayout();
            }
        });
        heightAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                final int height = (int) valueAnimator.getAnimatedValue();
                final ViewGroup.LayoutParams layoutParams = mImageView.getLayoutParams();
                layoutParams.height = height;
                mImageView.requestLayout();
            }
        });

        final ObjectAnimator rotateAnimation = ObjectAnimator.ofFloat(mImageView, "rotation",
                degrees);

        final AnimatorSet animations = new AnimatorSet();
        animations.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(final Animator animation) {
                isRotating = false;
                // Set ImageView size to match parent after rotation (in onMeasure())
                requestLayout();
            }
        });
        animations.play(widthAnimation)
                .with(heightAnimation)
                .with(rotateAnimation);

        animations.start();
    }
}
