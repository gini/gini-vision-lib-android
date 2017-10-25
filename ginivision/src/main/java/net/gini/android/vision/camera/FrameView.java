package net.gini.android.vision.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import net.gini.android.vision.R;

/**
 * @exclude
 */
public class FrameView extends View {

    private static final int INITIAL_OFFSET = 20;
    private static final int LINE_LENGTH = 70;
    private static final int LINE_WIDTH = 1;

    private int mHeight;
    private float mLineLength;
    private Paint mPaintLine;
    private int mWallOffsetSide;
    private int mWallOffsetTop;
    private int mWidth;
    private boolean mPortrait;

    public FrameView(final Context context) {
        this(context, null);
    }

    public FrameView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);

        mPaintLine = new Paint();
        mPaintLine.setColor(ContextCompat.getColor(context,R.color.gv_camera_preview_corners));
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
        drawBackgroundForButtons(canvas);
    }

    private void drawBackgroundForButtons(final Canvas canvas) {
        if(mPortrait) {
            final Paint paintRectangle = new Paint();
            paintRectangle.setStyle(Paint.Style.FILL);
            paintRectangle.setColor(Color.BLACK);
            paintRectangle.setAlpha(75);

            canvas.drawRect(mWallOffsetSide, mHeight-mWallOffsetTop -mLineLength, mWidth - mWallOffsetSide, mHeight - mWallOffsetTop, paintRectangle);
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        mPortrait = mWidth < mHeight;

        if(mPortrait) {
            mWallOffsetTop = dpToPx(INITIAL_OFFSET);
            int documentHeight = mHeight - mWallOffsetTop;
            int documentWidth = (int) (documentHeight / Math.sqrt(2)); //din a4
            mWallOffsetSide = (mWidth - documentWidth) / 2;
        } else {
            mWallOffsetSide = dpToPx(INITIAL_OFFSET);
            int documentWidth = mWidth - mWallOffsetSide;
            int documentHeight = (int) (documentWidth / Math.sqrt(2)); //din a4
            mWallOffsetTop = (mHeight - documentHeight) / 2;
        }

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
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
        canvas.drawLine(mWallOffsetSide, mHeight - mLineLength - mWallOffsetTop, mWallOffsetSide,
                mHeight - mWallOffsetTop, mPaintLine); // |
        canvas.drawLine(mWallOffsetSide, mHeight - mWallOffsetTop, mWallOffsetSide + mLineLength,
                mHeight - mWallOffsetTop, mPaintLine); // -
    }

    private void drawLowerRightLines(final Canvas canvas) {
        canvas.drawLine(mWidth - mWallOffsetSide, mHeight - mLineLength - mWallOffsetTop,
                mWidth - mWallOffsetSide, mHeight - mWallOffsetTop, mPaintLine); // |
        canvas.drawLine(mWidth - mLineLength - mWallOffsetSide, mHeight - mWallOffsetTop,
                mWidth - mWallOffsetSide, mHeight - mWallOffsetTop, mPaintLine); // -
    }

    private void drawShadowRectangles(final Canvas canvas) {
        Paint paintRectangle;
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
        canvas.drawRect(mWallOffsetSide, mHeight - mWallOffsetTop, mWidth - mWallOffsetSide,
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
