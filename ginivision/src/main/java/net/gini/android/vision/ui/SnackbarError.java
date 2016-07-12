package net.gini.android.vision.ui;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @exclude
 */
public class SnackbarError extends RelativeLayout {

    public static final int LENGTH_SHORT = 2000;
    public static final int LENGTH_LONG = 4000;
    public static final int LENGTH_INDEFINITE = Integer.MAX_VALUE;

    private static final int ANIM_DURATION = 250;
    private static final String TAG_SNACKBAR_ERROR = "GV_SNACKBAR_ERROR";

    private enum State {
        SHOWING, SHOWN,
        HIDING, HIDDEN
    }

    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private State mState = State.HIDDEN;
    private RelativeLayout mParentView;
    private int mShowDuration;
    private boolean mIsAttachedToWindow;
    private boolean mWaitForExisting = false;

    private TextView mTextView;
    private Button mButton;

    public static SnackbarError make(Context context,
                                     RelativeLayout parentView,
                                     String message,
                                     String buttonTitle,
                                     OnClickListener onClickListener,
                                     int duration) {
        SnackbarError snackbarError = new SnackbarError(context);
        snackbarError.setParentView(parentView);
        snackbarError.setMessage(message);
        snackbarError.setButtonTitle(buttonTitle);
        snackbarError.setButtonOnClickListener(onClickListener);
        snackbarError.setShowDuration(duration);
        return snackbarError;
    }

    public SnackbarError(Context context) {
        super(context);
        init();
    }

    public SnackbarError(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnackbarError(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.gv_snackbar_error, this);
        setTag(TAG_SNACKBAR_ERROR);
        bindViews();
    }

    private void bindViews() {
        mTextView = (TextView) findViewById(R.id.gv_text_error);
        mButton = (Button) findViewById(R.id.gv_button_error);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mIsAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mIsAttachedToWindow = false;
    }

    private void setMessage(String text) {
        mTextView.setText(text);
    }

    private void setButtonTitle(String title) {
        mButton.setText(title);
    }

    private void setButtonOnClickListener(final OnClickListener onClickListener) {
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
                hide();
            }
        });
    }

    private void setShowDuration(int showDuration) {
        mShowDuration = showDuration;
    }

    private void setParentView(RelativeLayout relativeLayout) {
        // Remove existing snackbars from the old parent, if present
        removeExistingSnackbarFromParentView();
        // Remove from the old parent
        removeFromParentView();
        mParentView = relativeLayout;
        // Remove existing snackbars from the new parent
        removeExistingSnackbarFromParentView();
    }

    private void addToParentView() {
        mParentView.addView(this);
    }

    private void removeFromParentView() {
        if (mParentView != null) {
            mParentView.removeView(this);
        }
        removeHandlerCallbacks(mHideRunnable);
    }

    private void removeHandlerCallbacks(Runnable runnable) {
        if (!mIsAttachedToWindow) {
            return;
        }
        getHandler().removeCallbacks(runnable);
    }

    private void removeExistingSnackbarFromParentView() {
        if (mParentView == null) {
            return;
        }
        List<SnackbarError> existingSnackbars = getExistingSnackbarsFromParentView();
        for (SnackbarError existingSnackbar : existingSnackbars) {
            existingSnackbar.hide();
        }
        mWaitForExisting = existingSnackbars.size() > 0;
    }

    private List<SnackbarError> getExistingSnackbarsFromParentView() {
        List<SnackbarError> existingSnackbars = new ArrayList<>();
        if (mParentView == null) {
            return existingSnackbars;
        }
        int childCount = mParentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = mParentView.getChildAt(i);
            Object tag = child.getTag();
            if (tag != null && tag.equals(TAG_SNACKBAR_ERROR) &&
                    child instanceof SnackbarError) {
                existingSnackbars.add((SnackbarError) child);
            }
        }
        return existingSnackbars;
    }

    public void show() {
        if (mState == State.SHOWING || mState == State.SHOWN) {
            return;
        }
        mState = State.SHOWING;

        addToParentView();
        setVisibility(View.INVISIBLE);
        // Delay to run after it has been measured and layouted
        post(new Runnable() {
            @Override
            public void run() {
                setTranslationY(getHeight());
                setVisibility(View.VISIBLE);

                // TODO: use AnimatorListenerNoOp after merging with MSDK-48
                animate()
                        .setStartDelay(mWaitForExisting ? ANIM_DURATION : 0)
                        .setDuration(ANIM_DURATION)
                        .translationY(0)
                        .setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mState = State.SHOWN;
                                postHideRunnable();
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
            }
        });
    }

    private void postHideRunnable() {
        if (mShowDuration == LENGTH_INDEFINITE) {
            return;
        }
        removeHandlerCallbacks(mHideRunnable);
        postToHandlerDelayed(mHideRunnable, mShowDuration);
    }

    private void postToHandlerDelayed(Runnable runnable, int duration) {
        if (!mIsAttachedToWindow) {
            return;
        }
        getHandler().postDelayed(runnable, duration);
    }

    public void hide() {
        if (mState == State.HIDING || mState == State.HIDDEN) {
            return;
        }
        mState = State.HIDING;

        removeHandlerCallbacks(mHideRunnable);

        // TODO: use AnimatorListenerNoOp after merging with MSDK-48
        animate()
                .setDuration(ANIM_DURATION)
                .translationY(getHeight())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mState = State.HIDDEN;
                        removeFromParentView();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
    }
}
