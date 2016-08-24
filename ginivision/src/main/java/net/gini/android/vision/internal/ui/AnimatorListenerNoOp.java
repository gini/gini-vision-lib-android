package net.gini.android.vision.internal.ui;

import android.animation.Animator;

/**
 * No-op implementation of the {@link Animator.AnimatorListener} interface to prevent implementing every
 * method when only a subset is required.
 * @exclude
 */
public class AnimatorListenerNoOp implements Animator.AnimatorListener {

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }
}
