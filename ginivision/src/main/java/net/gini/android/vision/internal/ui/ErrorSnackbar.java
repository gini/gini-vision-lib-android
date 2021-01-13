package net.gini.android.vision.internal.ui;

import static net.gini.android.vision.internal.ui.ErrorSnackbar.Position.BOTTOM;
import static net.gini.android.vision.internal.ui.ErrorSnackbar.Position.TOP;

import android.animation.Animator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.gini.android.vision.R;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

/**
 * Internal use only.
 *
 * @suppress
 */
public class ErrorSnackbar extends RelativeLayout {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorSnackbar.class);

    public static final int LENGTH_SHORT = 2000;
    public static final int LENGTH_LONG = 4000;
    public static final int LENGTH_INDEFINITE = Integer.MAX_VALUE;

    @VisibleForTesting
    static final int ANIM_DURATION = 250;
    private static final String TAG_SNACKBAR_ERROR = "GV_SNACKBAR_ERROR";
    private Position mPosition;

    private enum State {
        SHOWING,
        SHOWN,
        HIDING,
        HIDDEN
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public enum Position {
        TOP,
        BOTTOM
    }

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    private State mState = State.HIDDEN;
    private RelativeLayout mParentView;
    private int mShowDuration;
    private boolean mIsAttachedToWindow;
    private boolean mWaitForExisting;

    private TextView mTextView;
    private Button mButton;

    public static ErrorSnackbar make(@NonNull final Context context,
            @NonNull final RelativeLayout parentView,
            @NonNull final String message,
            @Nullable final String buttonTitle,
            @Nullable final OnClickListener onClickListener,
            final int duration) {
        return make(context, parentView, BOTTOM, message, buttonTitle, onClickListener,
                duration);
    }

    public static ErrorSnackbar make(@NonNull final Context context,
            @NonNull final RelativeLayout parentView,
            @NonNull final Position position,
            @NonNull final String message,
            @Nullable final String buttonTitle,
            @Nullable final OnClickListener onClickListener,
            final int duration) {
        final ErrorSnackbar errorSnackbar = new ErrorSnackbar(context);
        errorSnackbar.setParentView(parentView);
        errorSnackbar.setPosition(position);
        errorSnackbar.setMessage(message);
        errorSnackbar.setButtonTitle(buttonTitle);
        errorSnackbar.setButtonOnClickListener(onClickListener);
        errorSnackbar.setShowDuration(duration);
        return errorSnackbar;
    }

    public static void hideExisting(@NonNull final RelativeLayout parentView) {
        removeExistingSnackbarsFromParentView(parentView);
    }

    public ErrorSnackbar(final Context context) {
        super(context);
        init();
    }

    public ErrorSnackbar(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ErrorSnackbar(final Context context, final AttributeSet attrs, final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.gv_layout_snackbar_error, this);
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
        LOG.debug("Attached to window");
        mIsAttachedToWindow = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        LOG.debug("Detached from window");
        mIsAttachedToWindow = false;
    }

    private void setMessage(@NonNull final String text) {
        mTextView.setText(text);
    }

    private void setButtonTitle(@Nullable final String title) {
        if (TextUtils.isEmpty(title)) {
            return;
        }
        mButton.setVisibility(View.VISIBLE);
        mButton.setText(title);
    }

    private void setButtonOnClickListener(@Nullable final OnClickListener onClickListener) {
        if (onClickListener == null) {
            return;
        }
        mButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                hide();
                onClickListener.onClick(v);
            }
        });
    }

    private void setShowDuration(final int showDuration) {
        mShowDuration = showDuration;
    }

    private void setParentView(@NonNull final RelativeLayout parentView) {
        // Parent view cannot be changed (acts as final)
        if (mParentView != null) {
            LOG.warn("Parent view was already set to {}", mParentView);
            return;
        }
        mParentView = parentView;
        LOG.debug("Parent view set to {}", mParentView);
        // Remove existing snackbars from the parent
        final int removed = removeExistingSnackbarsFromParentView(mParentView);
        mWaitForExisting = removed > 0;
    }

    private void setPosition(final Position position) {
        mPosition = position;
    }

    private void addToParentView() {
        if (mParentView != null) {
            setParentAlignment();
            mParentView.addView(this);
            LOG.debug("Added to parent view {}", mParentView);
        } else {
            LOG.warn("No parent view to add to");
        }
    }

    private void setParentAlignment() {
        final LayoutParams layoutParams = getOrMakeLayoutParams();
        if (mPosition == TOP) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, TRUE);
        } else if (mPosition == BOTTOM) {
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, TRUE);
        } else {
            throw new UnsupportedOperationException("Unknown position: " + mPosition);
        }
        setLayoutParams(layoutParams);
    }

    @NonNull
    private LayoutParams getOrMakeLayoutParams() {
        LayoutParams layoutParams =
                (LayoutParams) getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        return layoutParams;
    }

    private void removeFromParentView() {
        if (mParentView != null) {
            mParentView.removeView(this);
            LOG.debug("Removed from parent view {}", mParentView);
        } else {
            LOG.warn("No parent view to remove from");
        }
        removeHandlerCallbacks(mHideRunnable);
    }

    private void removeHandlerCallbacks(@NonNull final Runnable runnable) {
        if (!mIsAttachedToWindow) {
            return;
        }
        getHandler().removeCallbacks(runnable);
        LOG.debug("Removed handler callbacks");
    }

    private static int removeExistingSnackbarsFromParentView(
            @NonNull final RelativeLayout parentView) {
        final List<ErrorSnackbar> existingSnackbars = getExistingSnackbarsFromParentView(
                parentView);
        for (final ErrorSnackbar existingSnackbar : existingSnackbars) {
            existingSnackbar.hide();
        }
        LOG.debug("Removed {} existing Snackbars from parent view {}", existingSnackbars.size(),
                parentView);
        return existingSnackbars.size();
    }

    @NonNull
    private static List<ErrorSnackbar> getExistingSnackbarsFromParentView(
            @NonNull final RelativeLayout parentView) {
        final List<ErrorSnackbar> existingSnackbars = new ArrayList<>();
        final int childCount = parentView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parentView.getChildAt(i);
            final Object tag = child.getTag();
            if (tag != null && tag.equals(TAG_SNACKBAR_ERROR)
                    && child instanceof ErrorSnackbar) {
                existingSnackbars.add((ErrorSnackbar) child);
            }
        }
        LOG.debug("Found {} existing Snackbars in parent view {}", existingSnackbars.size(),
                parentView);
        return existingSnackbars;
    }

    public void show() {
        doShow(true);
    }

    public void showWithoutAnimation() {
        doShow(false);
    }

    private void doShow(final boolean animated) {
        if (mState == State.SHOWING || mState == State.SHOWN) {
            LOG.debug("Already showing or shown");
            return;
        }
        mState = State.SHOWING;
        LOG.debug("Showing");

        addToParentView();
        setVisibility(View.INVISIBLE);
        // Delay to run after it has been measured and layouted
        post(new Runnable() {
            @Override
            public void run() {
                if (animated) {
                    if (mPosition == BOTTOM) {
                        setTranslationY(getHeight());
                    } else if (mPosition == TOP) {
                        setTranslationY(-getHeight());
                    }
                }
                setVisibility(View.VISIBLE);

                if (animated) {
                    animate()
                            .setStartDelay(mWaitForExisting ? ANIM_DURATION : 0)
                            .setDuration(ANIM_DURATION)
                            .translationY(0)
                            .setListener(new AnimatorListenerNoOp() {
                                @Override
                                public void onAnimationEnd(final Animator animation) {
                                    setStateToShown();
                                }
                            });
                } else {
                    setStateToShown();
                }
            }
        });
    }

    private void setStateToShown() {
        mState = State.SHOWN;
        LOG.debug("Shown");
        postHideRunnable();
    }

    private void postHideRunnable() {
        if (mShowDuration == LENGTH_INDEFINITE) {
            LOG.debug("Showing indefinitely");
            return;
        }
        removeHandlerCallbacks(mHideRunnable);
        postToHandlerDelayed(mHideRunnable, mShowDuration);
        LOG.debug("Showing for {}ms", mShowDuration);
    }

    private void postToHandlerDelayed(@NonNull final Runnable runnable, final int duration) {
        if (!mIsAttachedToWindow) {
            return;
        }
        getHandler().postDelayed(runnable, duration);
    }

    public void hide() {
        if (mState == State.HIDING || mState == State.HIDDEN) {
            LOG.debug("Already hiding or hidden");
            return;
        }
        mState = State.HIDING;
        LOG.debug("Hiding");

        removeHandlerCallbacks(mHideRunnable);

        int translationY = 0;
        if (mPosition == BOTTOM) {
            translationY = getHeight();
        } else if (mPosition == TOP) {
            translationY = -getHeight();
        }

        animate()
                .setDuration(ANIM_DURATION)
                .translationY(translationY)
                .setListener(new AnimatorListenerNoOp() {
                    @Override
                    public void onAnimationEnd(final Animator animation) {
                        mState = State.HIDDEN;
                        LOG.debug("Hidden");
                        removeFromParentView();
                    }
                });
    }
}
