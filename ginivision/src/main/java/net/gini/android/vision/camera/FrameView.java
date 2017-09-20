package net.gini.android.vision.camera;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class FrameView extends View {

    public static final int LINE_LENGTH = 56;
    public static final int LINE_WIDTH = 1;
    public static final int WALL_OFFSET = 24;

    private int mHeight;
    private float mLineLength;
    private Paint mPaintLine;
    private Paint mPaintRectangle;
    private int mWallOffset;
    private int mWidth;

    public FrameView(final Context context) {
        this(context, null);
    }

    public FrameView(final Context context, @Nullable final AttributeSet attrs) {
        super(context, attrs);
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.WHITE);
        mPaintLine.setStyle(Paint.Style.STROKE);


        mPaintRectangle = new Paint();
        mPaintRectangle.setStyle(Paint.Style.FILL);
        mPaintRectangle.setColor(Color.BLACK);
        mPaintRectangle.setAlpha(100);

        setLineWidth(LINE_WIDTH);
        setLineLength(LINE_LENGTH);
        setWallOffset(WALL_OFFSET);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawShadowRectangles(canvas);
        drawUpperLeftLines(canvas);
        drawLowerLeftLines(canvas);
        drawUpperRightLines(canvas);
        drawLowerRightLines(canvas);
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    public void setLineLength(final int dp) {
        mLineLength = dpToPx(dp);
        requestLayout();
        invalidate();
    }

    public void setWallOffset(final int dp) {
        mWallOffset = dpToPx(dp);
        requestLayout();
        invalidate();
    }

    private int dpToPx(final int dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void drawLowerLeftLines(final Canvas canvas) {
        canvas.drawLine(mWallOffset, mHeight - mLineLength - mWallOffset, mWallOffset,
                mHeight - mWallOffset, mPaintLine); // |
        canvas.drawLine(mWallOffset, mHeight - mWallOffset, mWallOffset + mLineLength,
                mHeight - mWallOffset, mPaintLine); // -
    }

    private void drawLowerRightLines(final Canvas canvas) {
        canvas.drawLine(mWidth - mWallOffset, mHeight - mLineLength - mWallOffset,
                mWidth - mWallOffset, mHeight - mWallOffset, mPaintLine); // |
        canvas.drawLine(mWidth - mLineLength - mWallOffset, mHeight - mWallOffset,
                mWidth - mWallOffset, mHeight - mWallOffset, mPaintLine); // -
    }

    private void drawShadowRectangles(final Canvas canvas) {
        //left
        canvas.drawRect(0, 0, mWallOffset, mHeight, mPaintRectangle);
        //right
        canvas.drawRect(mWidth - mWallOffset, 0, mWidth, mHeight, mPaintRectangle);
        //upper
        canvas.drawRect(mWallOffset, 0, mWidth - mWallOffset, mWallOffset, mPaintRectangle);
        //lower
        canvas.drawRect(mWallOffset, mHeight - mWallOffset, mWidth - mWallOffset, mHeight,
                mPaintRectangle);
    }

    private void drawUpperLeftLines(final Canvas canvas) {
        canvas.drawLine(mWallOffset, mWallOffset, mWallOffset, mWallOffset + mLineLength,
                mPaintLine); // |
        canvas.drawLine(mWallOffset, mWallOffset, mWallOffset + mLineLength, mWallOffset,
                mPaintLine); // -
    }

    private void drawUpperRightLines(final Canvas canvas) {
        canvas.drawLine(mWidth - mWallOffset, mWallOffset, mWidth - mWallOffset,
                mWallOffset + mLineLength, mPaintLine); // |
        canvas.drawLine(mWidth - mLineLength - mWallOffset, mWallOffset, mWidth - mWallOffset,
                mWallOffset, mPaintLine); // -
    }

    private void setLineWidth(final int dp) {
        mPaintLine.setStrokeWidth(dpToPx(dp));
    }
}
