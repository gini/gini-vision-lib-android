package net.gini.android.vision.internal.ui;

import android.animation.Animator;

/**
 * Internal use only.
 *
 * No-op implementation of the {@link Animator.AnimatorListener} interface to prevent implementing every
 * method when only a subset is required.
 *
 * @suppress
 */
public class AnimatorListenerNoOp implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(final Animator animation) {

    }

    @Override
    public void onAnimationEnd(final Animator animation) {

    }

    @Override
    public void onAnimationCancel(final Animator animation) {

    }

    @Override
    public void onAnimationRepeat(final Animator animation) {

    }
}
