package net.gini.android.vision.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import net.gini.android.vision.R;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Internal use only.
 *
 * @suppress
 */
public class FrameView extends View {

    private static final int INITIAL_OFFSET = 20;
    private static final int LINE_LENGTH = 76;
    private static final int LINE_WIDTH = 1;

    private int mHeight;
    private float mLineLength;
    private final Paint mPaintLine;
    private int mWallOffsetSide;
    private int mWallOffsetTop;
    private int mWallOffsetBottom;
    private int mWidth;
    private boolean mShouldDrawBackgroundForButtons;

    public FrameView(final Context context) {
        this(context, null);
    }

    public FrameView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);

        mPaintLine = new Paint();
        mPaintLine.setColor(ContextCompat.getColor(context, R.color.gv_camera_preview_corners));
        mPaintLine.setStyle(Paint.Style.STROKE);

        setLineWidth(LINE_WIDTH);
        setLineLength(LINE_LENGTH);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawShadowRectangles(canvas);
        drawUpperLeftLines(canvas);
        drawLowerLeftLines(canvas);
        drawUpperRightLines(canvas);
        drawLowerRightLines(canvas);
        if (mShouldDrawBackgroundForButtons) {
            drawBackgroundForButtons(canvas);
        }
    }

    private void drawBackgroundForButtons(final Canvas canvas) {
        final Paint paintRectangle = new Paint();
        paintRectangle.setStyle(Paint.Style.FILL);
        paintRectangle.setColor(Color.BLACK);
        paintRectangle.setAlpha(75);

        canvas.drawRect(mWallOffsetSide,
                mHeight - mWallOffsetTop - mLineLength,
                mWidth - mWallOffsetSide,
                mHeight - mWallOffsetBottom,
                paintRectangle);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        final boolean portrait = mWidth < mHeight;

        if (portrait) {
            measurePortrait();
        } else {
            measureLandscape();
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    private void measurePortrait() {
        final int initialOffset = dpToPx(INITIAL_OFFSET);
        final double sqrt2 = Math.sqrt(2);

        // Fit height and center horizontally
        mShouldDrawBackgroundForButtons = true;
        mWallOffsetTop = initialOffset;
        mWallOffsetBottom = initialOffset;
        int documentHeight = mHeight - mWallOffsetTop - mWallOffsetBottom;
        int documentWidth = (int) (documentHeight / sqrt2); //din a4
        mWallOffsetSide = (mWidth - documentWidth) / 2;
        // If A4 width is greater than the view's width
        if (mWallOffsetSide < 0) {
            // Fit width and align to top
            mShouldDrawBackgroundForButtons = false;
            mWallOffsetSide = initialOffset;
            documentWidth = mWidth - 2 * mWallOffsetSide;
            documentHeight = (int) (documentWidth * sqrt2);
            mWallOffsetBottom = mHeight - documentHeight - mWallOffsetTop;
        }
    }

    private void measureLandscape() {
        final int initialOffset = dpToPx(INITIAL_OFFSET);
        final double sqrt2 = Math.sqrt(2);

        // Fit width and center vertically
        mShouldDrawBackgroundForButtons = false;
        mWallOffsetSide = initialOffset;
        int documentWidth = mWidth - 2 * mWallOffsetSide;
        int documentHeight = (int) (documentWidth / sqrt2);
        mWallOffsetTop = (mHeight - documentHeight) / 2;
        mWallOffsetBottom = mWallOffsetTop;
        // If A4 height is greater than the view's height
        if (mWallOffsetTop < 0) {
            // Fit height and center horizontally
            mWallOffsetTop = initialOffset;
            mWallOffsetBottom = initialOffset;
            documentHeight = mHeight - mWallOffsetTop - mWallOffsetBottom;
            documentWidth = (int) (documentHeight * sqrt2); //din a4
            mWallOffsetSide = (mWidth - documentWidth) / 2;
        }
    }

    public void setLineLength(final int dp) {
        mLineLength = dpToPx(dp);
        requestLayout();
        invalidate();
    }


    private int dpToPx(final int dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void drawLowerLeftLines(final Canvas canvas) {
        canvas.drawLine(mWallOffsetSide,
                mHeight - mLineLength - mWallOffsetBottom,
                mWallOffsetSide,
                mHeight - mWallOffsetBottom,
                mPaintLine); // |
        canvas.drawLine(mWallOffsetSide,
                mHeight - mWallOffsetBottom,
                mWallOffsetSide + mLineLength,
                mHeight - mWallOffsetBottom,
                mPaintLine); // -
    }

    private void drawLowerRightLines(final Canvas canvas) {
        canvas.drawLine(mWidth - mWallOffsetSide,
                mHeight - mLineLength - mWallOffsetBottom,
                mWidth - mWallOffsetSide,
                mHeight - mWallOffsetBottom,
                mPaintLine); // |
        canvas.drawLine(mWidth - mLineLength - mWallOffsetSide,
                mHeight - mWallOffsetBottom,
                mWidth - mWallOffsetSide,
                mHeight - mWallOffsetBottom,
                mPaintLine); // -
    }

    private void drawShadowRectangles(final Canvas canvas) {
        final Paint paintRectangle;
        paintRectangle = new Paint();
        paintRectangle.setStyle(Paint.Style.FILL);
        paintRectangle.setColor(Color.BLACK);
        paintRectangle.setAlpha(175);

        //left
        canvas.drawRect(0, 0, mWallOffsetSide, mHeight, paintRectangle);
        //right
        canvas.drawRect(mWidth - mWallOffsetSide, 0, mWidth, mHeight, paintRectangle);
        //upper
        canvas.drawRect(mWallOffsetSide, 0, mWidth - mWallOffsetSide, mWallOffsetTop,
                paintRectangle);
        //lower
        canvas.drawRect(mWallOffsetSide, mHeight - mWallOffsetBottom, mWidth - mWallOffsetSide,
                mHeight, paintRectangle);
    }

    private void drawUpperLeftLines(final Canvas canvas) {
        canvas.drawLine(mWallOffsetSide, mWallOffsetTop, mWallOffsetSide,
                mWallOffsetTop + mLineLength, mPaintLine); // |
        canvas.drawLine(mWallOffsetSide, mWallOffsetTop, mWallOffsetSide + mLineLength,
                mWallOffsetTop, mPaintLine); // -
    }

    private void drawUpperRightLines(final Canvas canvas) {
        canvas.drawLine(mWidth - mWallOffsetSide, mWallOffsetTop, mWidth - mWallOffsetSide,
                mWallOffsetTop + mLineLength, mPaintLine); // |
        canvas.drawLine(mWidth - mLineLength - mWallOffsetSide, mWallOffsetTop,
                mWidth - mWallOffsetSide, mWallOffsetTop, mPaintLine); // -
    }

    private void setLineWidth(final int dp) {
        mPaintLine.setStrokeWidth(dpToPx(dp));
    }
}
