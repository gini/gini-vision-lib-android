/*
 * TouchImageView.java
 * By: Michael Ortiz
 * Updated By: Patrick Lackemacher
 * Updated By: Babay88
 * Updated By: @ipsilondev
 * Updated By: hank-cp
 * Updated By: singpolyma
 * -------------------
 * Extends Android ImageView to include pinch zooming, panning, fling and double tap zoom.
 */

package com.ortiz.touch;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.OverScroller;
import android.widget.Scroller;

public class TouchImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String DEBUG = "DEBUG";

    //
    // SuperMin and SuperMax multipliers. Determine how much the image can be
    // zoomed below or above the zoom boundaries, before animating back to the
    // min/max zoom boundary.
    //
    private static final float SUPER_MIN_MULTIPLIER = 1.f;
    private static final float SUPER_MAX_MULTIPLIER = 1.f;

    //
    // Scale of image ranges from minScale to maxScale, where minScale == 1
    // when the image is stretched to fit view.
    //
    private float normalizedScale;

    //
    // Matrix applied to image. MSCALE_X and MSCALE_Y should always be equal.
    // MTRANS_X and MTRANS_Y are the other values used. prevMatrix is the matrix
    // saved prior to the screen rotating.
    //
    private Matrix matrix, prevMatrix;

    private static enum State {
        NONE,
        DRAG,
        ZOOM,
        FLING,
        ANIMATE_ZOOM
    }

    private State state;

    private float minScale;
    private float maxScale;
    private float superMinScale;
    private float superMaxScale;
    private float[] m;

    private Context context;
    private Fling fling;

    private ScaleType mScaleType;

    private boolean onDrawReady;

    private ZoomVariables delayedZoomVariables;

    //
    // Size of view and previous view size (ie before rotation)
    //
    private int viewWidth, viewHeight, prevViewWidth, prevViewHeight;

    //
    // Size of image when it is stretched to fit view. Before and After rotation.
    //
    private float matchViewWidth, matchViewHeight, prevMatchViewWidth, prevMatchViewHeight;

    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private GestureDetector.OnDoubleTapListener doubleTapListener = null;
    private OnTouchListener userTouchListener = null;
    private OnTouchImageViewListener touchImageViewListener = null;

    public TouchImageView(final Context context) {
        super(context);
        sharedConstructing(context);
    }

    public TouchImageView(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        sharedConstructing(context);
    }

    public TouchImageView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructing(context);
    }

    private void sharedConstructing(final Context context) {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());
        matrix = new Matrix();
        prevMatrix = new Matrix();
        m = new float[9];
        normalizedScale = 1;
        if (mScaleType == null) {
            mScaleType = ScaleType.FIT_CENTER;
        }
        minScale = 1;
        maxScale = 3;
        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
        setImageMatrix(matrix);
        setScaleType(ScaleType.MATRIX);
        setState(State.NONE);
        onDrawReady = false;
        super.setOnTouchListener(new PrivateOnTouchListener());
    }

    @Override
    public void setOnTouchListener(final OnTouchListener l) {
        userTouchListener = l;
    }

    public void setOnTouchImageViewListener(final OnTouchImageViewListener l) {
        touchImageViewListener = l;
    }

    public void setOnDoubleTapListener(final GestureDetector.OnDoubleTapListener l) {
        doubleTapListener = l;
    }

    @Override
    public void setImageResource(final int resId) {
        super.setImageResource(resId);
        savePreviousImageValues();
        fitImageToView();
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        super.setImageBitmap(bm);
        savePreviousImageValues();
        fitImageToView();
    }

    @Override
    public void setImageDrawable(final Drawable drawable) {
        super.setImageDrawable(drawable);
        savePreviousImageValues();
        fitImageToView();
    }

    @Override
    public void setImageURI(final Uri uri) {
        super.setImageURI(uri);
        savePreviousImageValues();
        fitImageToView();
    }

    @Override
    public void setScaleType(final ScaleType type) {
        if (type == ScaleType.FIT_START || type == ScaleType.FIT_END) {
            throw new UnsupportedOperationException(
                    "TouchImageView does not support FIT_START or FIT_END");
        }
        if (type == ScaleType.MATRIX) {
            super.setScaleType(ScaleType.MATRIX);

        } else {
            mScaleType = type;
            if (onDrawReady) {
                //
                // If the image is already rendered, scaleType has been called programmatically
                // and the TouchImageView should be updated with the new scaleType.
                //
                setZoom(this);
            }
        }
    }

    @Override
    public ScaleType getScaleType() {
        return mScaleType;
    }

    /**
     * Returns false if image is in initial, unzoomed state. False, otherwise.
     * @return true if image is zoomed
     */
    public boolean isZoomed() {
        return normalizedScale != 1;
    }

    /**
     * Return a Rect representing the zoomed image.
     * @return rect representing zoomed image
     */
    public RectF getZoomedRect() {
        if (mScaleType == ScaleType.FIT_XY) {
            throw new UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
        }
        final PointF topLeft = transformCoordTouchToBitmap(0, 0, true);
        final PointF bottomRight = transformCoordTouchToBitmap(viewWidth, viewHeight, true);

        final float w = getDrawable().getIntrinsicWidth();
        final float h = getDrawable().getIntrinsicHeight();
        return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
    }

    /**
     * Save the current matrix and view dimensions
     * in the prevMatrix and prevView variables.
     */
    private void savePreviousImageValues() {
        if (matrix != null && viewHeight != 0 && viewWidth != 0) {
            matrix.getValues(m);
            prevMatrix.setValues(m);
            prevMatchViewHeight = matchViewHeight;
            prevMatchViewWidth = matchViewWidth;
            prevViewHeight = viewHeight;
            prevViewWidth = viewWidth;
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putFloat("saveScale", normalizedScale);
        bundle.putFloat("matchViewHeight", matchViewHeight);
        bundle.putFloat("matchViewWidth", matchViewWidth);
        bundle.putInt("viewWidth", viewWidth);
        bundle.putInt("viewHeight", viewHeight);
        matrix.getValues(m);
        bundle.putFloatArray("matrix", m);
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(final Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            normalizedScale = bundle.getFloat("saveScale");
            m = bundle.getFloatArray("matrix");
            prevMatrix.setValues(m);
            prevMatchViewHeight = bundle.getFloat("matchViewHeight");
            prevMatchViewWidth = bundle.getFloat("matchViewWidth");
            prevViewHeight = bundle.getInt("viewHeight");
            prevViewWidth = bundle.getInt("viewWidth");
            super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
            return;
        }

        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        onDrawReady = true;
        if (delayedZoomVariables != null) {
            setZoom(delayedZoomVariables.scale, delayedZoomVariables.focusX,
                    delayedZoomVariables.focusY, delayedZoomVariables.scaleType);
            delayedZoomVariables = null;
        }
        super.onDraw(canvas);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        savePreviousImageValues();
    }

    /**
     * Get the max zoom multiplier.
     * @return max zoom multiplier.
     */
    public float getMaxZoom() {
        return maxScale;
    }

    /**
     * Set the max zoom multiplier. Default value: 3.
     * @param max max zoom multiplier.
     */
    public void setMaxZoom(final float max) {
        maxScale = max;
        superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
    }

    /**
     * Get the min zoom multiplier.
     * @return min zoom multiplier.
     */
    public float getMinZoom() {
        return minScale;
    }

    /**
     * Get the current zoom. This is the zoom relative to the initial
     * scale, not the original resource.
     * @return current zoom multiplier.
     */
    public float getCurrentZoom() {
        return normalizedScale;
    }

    /**
     * Set the min zoom multiplier. Default value: 1.
     * @param min min zoom multiplier.
     */
    public void setMinZoom(final float min) {
        minScale = min;
        superMinScale = SUPER_MIN_MULTIPLIER * minScale;
    }

    /**
     * Reset zoom and translation to initial state.
     */
    public void resetZoom() {
        normalizedScale = 1;
        fitImageToView();
    }

    /**
     * Set zoom to the specified scale. Image will be centered by default.
     * @param scale
     */
    public void setZoom(final float scale) {
        setZoom(scale, 0.5f, 0.5f);
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     * @param scale
     * @param focusX
     * @param focusY
     */
    public void setZoom(final float scale, final float focusX, final float focusY) {
        setZoom(scale, focusX, focusY, mScaleType);
    }

    /**
     * Set zoom to the specified scale. Image will be centered around the point
     * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
     * as a fraction from the left and top of the view. For example, the top left
     * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
     * @param scale
     * @param focusX
     * @param focusY
     * @param scaleType
     */
    public void setZoom(final float scale, final float focusX, final float focusY, final ScaleType scaleType) {
        //
        // setZoom can be called before the image is on the screen, but at this point,
        // image and view sizes have not yet been calculated in onMeasure. Thus, we should
        // delay calling setZoom until the view has been measured.
        //
        if (!onDrawReady) {
            delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
            return;
        }

        if (scaleType != mScaleType) {
            setScaleType(scaleType);
        }
        resetZoom();
        scaleImage(scale, viewWidth / 2f, viewHeight / 2f, true);
        matrix.getValues(m);
        m[Matrix.MTRANS_X] = -((focusX * getImageWidth()) - (viewWidth * 0.5f));
        m[Matrix.MTRANS_Y] = -((focusY * getImageHeight()) - (viewHeight * 0.5f));
        matrix.setValues(m);
        fixTrans();
        setImageMatrix(matrix);
    }

    /**
     * Set zoom parameters equal to another TouchImageView. Including scale, position,
     * and ScaleType.
     * @param TouchImageView
     */
    public void setZoom(final TouchImageView img) {
        final PointF center = img.getScrollPosition();
        setZoom(img.getCurrentZoom(), center.x, center.y, img.getScaleType());
    }

    /**
     * Return the point at the center of the zoomed image. The PointF coordinates range
     * in value between 0 and 1 and the focus point is denoted as a fraction from the left
     * and top of the view. For example, the top left corner of the image would be (0, 0).
     * And the bottom right corner would be (1, 1).
     * @return PointF representing the scroll position of the zoomed image.
     */
    public PointF getScrollPosition() {
        final Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }
        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        final PointF point = transformCoordTouchToBitmap(viewWidth / 2f, viewHeight / 2f, true);
        point.x /= drawableWidth;
        point.y /= drawableHeight;
        return point;
    }

    /**
     * Set the focus point of the zoomed image. The focus points are denoted as a fraction from the
     * left and top of the view. The focus points can range in value between 0 and 1.
     * @param focusX
     * @param focusY
     */
    public void setScrollPosition(final float focusX, final float focusY) {
        setZoom(normalizedScale, focusX, focusY);
    }

    /**
     * Performs boundary checking and fixes the image matrix if it
     * is out of bounds.
     */
    private void fixTrans() {
        matrix.getValues(m);
        final float transX = m[Matrix.MTRANS_X];
        final float transY = m[Matrix.MTRANS_Y];

        final float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
        final float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());

        if (fixTransX != 0 || fixTransY != 0) {
            matrix.postTranslate(fixTransX, fixTransY);
        }
    }

    /**
     * When transitioning from zooming from focus to zoom from center (or vice versa)
     * the image can become unaligned within the view. This is apparent when zooming
     * quickly. When the content size is less than the view size, the content will often
     * be centered incorrectly within the view. fixScaleTrans first calls fixTrans() and
     * then makes sure the image is centered correctly within the view.
     */
    private void fixScaleTrans() {
        fixTrans();
        matrix.getValues(m);
        if (getImageWidth() < viewWidth) {
            m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
        }

        if (getImageHeight() < viewHeight) {
            m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
        }
        matrix.setValues(m);
    }

    private float getFixTrans(final float trans, final float viewSize, final float contentSize) {
        final float minTrans;
        final float maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;

        } else {
            minTrans = viewSize - contentSize;
            maxTrans = 0;
        }

        if (trans < minTrans) {
            return -trans + minTrans;
        }
        if (trans > maxTrans) {
            return -trans + maxTrans;
        }
        return 0;
    }

    private float getFixDragTrans(final float delta, final float viewSize, final float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    private float getImageWidth() {
        return matchViewWidth * normalizedScale;
    }

    private float getImageHeight() {
        return matchViewHeight * normalizedScale;
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0
                || drawable.getIntrinsicHeight() == 0) {
            setMeasuredDimension(0, 0);
            return;
        }

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        viewWidth = setViewSize(widthMode, widthSize, drawableWidth);
        viewHeight = setViewSize(heightMode, heightSize, drawableHeight);

        //
        // Set view dimensions
        //
        setMeasuredDimension(viewWidth, viewHeight);

        //
        // Fit content within view
        //
        fitImageToView();
    }

    /**
     * If the normalizedScale is equal to 1, then the image is made to fit the screen. Otherwise,
     * it is made to fit the screen according to the dimensions of the previous image matrix. This
     * allows the image to maintain its zoom after rotation.
     */
    private void fitImageToView() {
        final Drawable drawable = getDrawable();
        if (drawable == null || drawable.getIntrinsicWidth() == 0
                || drawable.getIntrinsicHeight() == 0) {
            return;
        }
        if (matrix == null || prevMatrix == null) {
            return;
        }

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();

        //
        // Scale image for view
        //
        float scaleX = (float) viewWidth / drawableWidth;
        float scaleY = (float) viewHeight / drawableHeight;

        switch (mScaleType) {
            case CENTER:
                scaleX = scaleY = 1;
                break;

            case CENTER_CROP:
                scaleX = scaleY = Math.max(scaleX, scaleY);
                break;

            case CENTER_INSIDE:
                scaleX = scaleY = Math.min(1, Math.min(scaleX, scaleY));
                break;
            case FIT_CENTER:
                scaleX = scaleY = Math.min(scaleX, scaleY);
                break;

            case FIT_XY:
                break;

            default:
                //
                // FIT_START and FIT_END not supported
                //
                throw new UnsupportedOperationException(
                        "TouchImageView does not support FIT_START or FIT_END");

        }

        //
        // Center the image
        //
        final float redundantXSpace = viewWidth - (scaleX * drawableWidth);
        final float redundantYSpace = viewHeight - (scaleY * drawableHeight);
        matchViewWidth = viewWidth - redundantXSpace;
        matchViewHeight = viewHeight - redundantYSpace;
        if (!isZoomed()) {
            //
            // Stretch and center image to fit view
            //
            matrix.setScale(scaleX, scaleY);
            matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
            normalizedScale = 1;

        } else {
            //
            // These values should never be 0 or we will set viewWidth and viewHeight
            // to NaN in translateMatrixAfterRotate. To avoid this, call savePreviousImageValues
            // to set them equal to the current values.
            //
            if (prevMatchViewWidth == 0 || prevMatchViewHeight == 0) {
                savePreviousImageValues();
            }

            prevMatrix.getValues(m);

            //
            // Rescale Matrix after rotation
            //
            m[Matrix.MSCALE_X] = matchViewWidth / drawableWidth * normalizedScale;
            m[Matrix.MSCALE_Y] = matchViewHeight / drawableHeight * normalizedScale;

            //
            // TransX and TransY from previous matrix
            //
            final float transX = m[Matrix.MTRANS_X];
            final float transY = m[Matrix.MTRANS_Y];

            //
            // Width
            //
            final float prevActualWidth = prevMatchViewWidth * normalizedScale;
            final float actualWidth = getImageWidth();
            translateMatrixAfterRotate(Matrix.MTRANS_X, transX, prevActualWidth, actualWidth,
                    prevViewWidth, viewWidth, drawableWidth);

            //
            // Height
            //
            final float prevActualHeight = prevMatchViewHeight * normalizedScale;
            final float actualHeight = getImageHeight();
            translateMatrixAfterRotate(Matrix.MTRANS_Y, transY, prevActualHeight, actualHeight,
                    prevViewHeight, viewHeight, drawableHeight);

            //
            // Set the matrix to the adjusted scale and translate values.
            //
            matrix.setValues(m);
        }
        fixTrans();
        setImageMatrix(matrix);
    }

    /**
     * Set view dimensions based on layout params
     *
     * @param mode
     * @param size
     * @param drawableWidth
     * @return
     */
    private int setViewSize(final int mode, final int size, final int drawableWidth) {
        final int viewSize;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                viewSize = size;
                break;

            case MeasureSpec.AT_MOST:
                viewSize = Math.min(drawableWidth, size);
                break;

            case MeasureSpec.UNSPECIFIED:
                viewSize = drawableWidth;
                break;

            default:
                viewSize = size;
                break;
        }
        return viewSize;
    }

    /**
     * After rotating, the matrix needs to be translated. This function finds the area of image
     * which was previously centered and adjusts translations so that is again the center, post-rotation.
     *
     * @param axis Matrix.MTRANS_X or Matrix.MTRANS_Y
     * @param trans the value of trans in that axis before the rotation
     * @param prevImageSize the width/height of the image before the rotation
     * @param imageSize width/height of the image after rotation
     * @param prevViewSize width/height of view before rotation
     * @param viewSize width/height of view after rotation
     * @param drawableSize width/height of drawable
     */
    private void translateMatrixAfterRotate(final int axis, final float trans, final float prevImageSize,
            final float imageSize, final int prevViewSize, final int viewSize, final int drawableSize) {
        if (imageSize < viewSize) {
            //
            // The width/height of image is less than the view's width/height. Center it.
            //
            m[axis] = (viewSize - (drawableSize * m[Matrix.MSCALE_X])) * 0.5f;

        } else if (trans > 0) {
            //
            // The image is larger than the view, but was not before rotation. Center it.
            //
            m[axis] = -((imageSize - viewSize) * 0.5f);

        } else {
            //
            // Find the area of the image which was previously centered in the view. Determine its distance
            // from the left/top side of the view as a fraction of the entire image's width/height. Use that percentage
            // to calculate the trans in the new view width/height.
            //
            final float percentage = (Math.abs(trans) + (0.5f * prevViewSize)) / prevImageSize;
            m[axis] = -((percentage * imageSize) - (viewSize * 0.5f));
        }
    }

    private void setState(final State state) {
        this.state = state;
    }

    public boolean canScrollHorizontallyFroyo(final int direction) {
        return canScrollHorizontally(direction);
    }

    @Override
    public boolean canScrollHorizontally(final int direction) {
        matrix.getValues(m);
        final float x = m[Matrix.MTRANS_X];

        if (getImageWidth() < viewWidth) {
            return false;

        } else if (x >= -1 && direction < 0) {
            return false;

        } else if (Math.abs(x) + viewWidth + 1 >= getImageWidth() && direction > 0) {
            return false;
        }

        return true;
    }

    /**
     * Gesture Delegate detects a single click or long click and passes that on
     * to the view's listener.
     * @author Ortiz
     *
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(final MotionEvent e) {
            if (doubleTapListener != null) {
                return doubleTapListener.onSingleTapConfirmed(e);
            }
            return performClick();
        }

        @Override
        public void onLongPress(final MotionEvent e) {
            performLongClick();
        }

        @Override
        public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
            if (fling != null) {
                //
                // If a previous fling is still active, it should be cancelled so that two flings
                // are not run simultaenously.
                //
                fling.cancelFling();
            }
            fling = new Fling((int) velocityX, (int) velocityY);
            compatPostOnAnimation(fling);
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onDoubleTap(final MotionEvent e) {
            boolean consumed = false;
            if (doubleTapListener != null) {
                consumed = doubleTapListener.onDoubleTap(e);
            }
            if (state == State.NONE) {
                final float targetZoom = (normalizedScale == minScale) ? maxScale : minScale;
                final DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, e.getX(), e.getY(), false);
                compatPostOnAnimation(doubleTap);
                consumed = true;
            }
            return consumed;
        }

        @Override
        public boolean onDoubleTapEvent(final MotionEvent e) {
            if (doubleTapListener != null) {
                return doubleTapListener.onDoubleTapEvent(e);
            }
            return false;
        }
    }

    public interface OnTouchImageViewListener {
        void onMove();

        void onZoom();
    }

    /**
     * Responsible for all touch events. Handles the heavy lifting of drag and also sends
     * touch events to Scale Detector and Gesture Detector.
     * @author Ortiz
     *
     */
    private class PrivateOnTouchListener implements OnTouchListener {

        //
        // Remember last point position for dragging
        //
        private final PointF last = new PointF();

        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            mScaleDetector.onTouchEvent(event);
            mGestureDetector.onTouchEvent(event);
            final PointF curr = new PointF(event.getX(), event.getY());

            if (state == State.NONE || state == State.DRAG || state == State.FLING) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        last.set(curr);
                        if (fling != null) {
                            fling.cancelFling();
                        }
                        setState(State.DRAG);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (state == State.DRAG) {
                            final float deltaX = curr.x - last.x;
                            final float deltaY = curr.y - last.y;
                            final float fixTransX = getFixDragTrans(deltaX, viewWidth, getImageWidth());
                            final float fixTransY = getFixDragTrans(deltaY, viewHeight, getImageHeight());
                            matrix.postTranslate(fixTransX, fixTransY);
                            fixTrans();
                            last.set(curr.x, curr.y);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        setState(State.NONE);
                        break;
                    default:
                        break;
                }
            }

            setImageMatrix(matrix);

            //
            // User-defined OnTouchListener
            //
            if (userTouchListener != null) {
                userTouchListener.onTouch(v, event);
            }

            //
            // OnTouchImageViewListener is set: TouchImageView dragged by user.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener.onMove();
            }

            //
            // indicate event was handled
            //
            return true;
        }
    }

    /**
     * ScaleListener detects user two finger scaling and scales image.
     * @author Ortiz
     *
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(final ScaleGestureDetector detector) {
            setState(State.ZOOM);
            return true;
        }

        @Override
        public boolean onScale(final ScaleGestureDetector detector) {
            scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);

            //
            // OnTouchImageViewListener is set: TouchImageView pinch zoomed by user.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener.onZoom();
            }
            return true;
        }

        @Override
        public void onScaleEnd(final ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            setState(State.NONE);
            boolean animateToZoomBoundary = false;
            float targetZoom = normalizedScale;
            if (normalizedScale > maxScale) {
                targetZoom = maxScale;
                animateToZoomBoundary = true;

            } else if (normalizedScale < minScale) {
                targetZoom = minScale;
                animateToZoomBoundary = true;
            }

            if (animateToZoomBoundary) {
                final DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, viewWidth / 2f,
                        viewHeight / 2f, true);
                compatPostOnAnimation(doubleTap);
            }
        }
    }

    private void scaleImage(double deltaScale, final float focusX, final float focusY,
            final boolean stretchImageToSuper) {

        final float lowerScale;
        final float upperScale;
        if (stretchImageToSuper) {
            lowerScale = superMinScale;
            upperScale = superMaxScale;

        } else {
            lowerScale = minScale;
            upperScale = maxScale;
        }

        final float origScale = normalizedScale;
        normalizedScale *= deltaScale;
        if (normalizedScale > upperScale) {
            normalizedScale = upperScale;
            deltaScale = upperScale / origScale;
        } else if (normalizedScale < lowerScale) {
            normalizedScale = lowerScale;
            deltaScale = lowerScale / origScale;
        }

        matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
        fixScaleTrans();
    }

    /**
     * DoubleTapZoom calls a series of runnables which apply
     * an animated zoom in/out graphic to the image.
     * @author Ortiz
     *
     */
    private class DoubleTapZoom implements Runnable {

        private final long startTime;
        private static final float ZOOM_TIME = 500;
        private final float startZoom;
        private final float targetZoom;
        private final float bitmapX;
        private final float bitmapY;
        private final boolean stretchImageToSuper;
        private final AccelerateDecelerateInterpolator interpolator =
                new AccelerateDecelerateInterpolator();
        private final PointF startTouch;
        private final PointF endTouch;

        DoubleTapZoom(final float targetZoom, final float focusX, final float focusY, final boolean stretchImageToSuper) {
            setState(State.ANIMATE_ZOOM);
            startTime = System.currentTimeMillis();
            startZoom = normalizedScale;
            this.targetZoom = targetZoom;
            this.stretchImageToSuper = stretchImageToSuper;
            final PointF bitmapPoint = transformCoordTouchToBitmap(focusX, focusY, false);
            bitmapX = bitmapPoint.x;
            bitmapY = bitmapPoint.y;

            //
            // Used for translating image during scaling
            //
            startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY);
            endTouch = new PointF(viewWidth / 2f, viewHeight / 2f);
        }

        @Override
        public void run() {
            final float t = interpolate();
            final double deltaScale = calculateDeltaScale(t);
            scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper);
            translateImageToCenterTouchPosition(t);
            fixScaleTrans();
            setImageMatrix(matrix);

            //
            // OnTouchImageViewListener is set: double tap runnable updates listener
            // with every frame.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener.onMove();
            }

            if (t < 1f) {
                //
                // We haven't finished zooming
                //
                compatPostOnAnimation(this);

            } else {
                //
                // Finished zooming
                //
                setState(State.NONE);
            }
        }

        /**
         * Interpolate between where the image should start and end in order to translate
         * the image so that the point that is touched is what ends up centered at the end
         * of the zoom.
         * @param t
         */
        private void translateImageToCenterTouchPosition(final float t) {
            final float targetX = startTouch.x + t * (endTouch.x - startTouch.x);
            final float targetY = startTouch.y + t * (endTouch.y - startTouch.y);
            final PointF curr = transformCoordBitmapToTouch(bitmapX, bitmapY);
            matrix.postTranslate(targetX - curr.x, targetY - curr.y);
        }

        /**
         * Use interpolator to get t
         * @return
         */
        private float interpolate() {
            final long currTime = System.currentTimeMillis();
            float elapsed = (currTime - startTime) / ZOOM_TIME;
            elapsed = Math.min(1f, elapsed);
            return interpolator.getInterpolation(elapsed);
        }

        /**
         * Interpolate the current targeted zoom and get the delta
         * from the current zoom.
         * @param t
         * @return
         */
        private double calculateDeltaScale(final float t) {
            final double zoom = startZoom + t * (targetZoom - startZoom);
            return zoom / normalizedScale;
        }
    }

    /**
     * This function will transform the coordinates in the touch event to the coordinate
     * system of the drawable that the imageview contain
     * @param x x-coordinate of touch event
     * @param y y-coordinate of touch event
     * @param clipToBitmap Touch event may occur within view, but outside image content. True, to clip return value
     * 			to the bounds of the bitmap size.
     * @return Coordinates of the point touched, in the coordinate system of the original drawable.
     */
    private PointF transformCoordTouchToBitmap(final float x, final float y, final boolean clipToBitmap) {
        matrix.getValues(m);
        final float origW = getDrawable().getIntrinsicWidth();
        final float origH = getDrawable().getIntrinsicHeight();
        final float transX = m[Matrix.MTRANS_X];
        final float transY = m[Matrix.MTRANS_Y];
        float finalX = ((x - transX) * origW) / getImageWidth();
        float finalY = ((y - transY) * origH) / getImageHeight();

        if (clipToBitmap) {
            finalX = Math.min(Math.max(finalX, 0), origW);
            finalY = Math.min(Math.max(finalY, 0), origH);
        }

        return new PointF(finalX, finalY);
    }

    /**
     * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
     * drawable's coordinate system to the view's coordinate system.
     * @param bx x-coordinate in original bitmap coordinate system
     * @param by y-coordinate in original bitmap coordinate system
     * @return Coordinates of the point in the view's coordinate system.
     */
    private PointF transformCoordBitmapToTouch(final float bx, final float by) {
        matrix.getValues(m);
        final float origW = getDrawable().getIntrinsicWidth();
        final float origH = getDrawable().getIntrinsicHeight();
        final float px = bx / origW;
        final float py = by / origH;
        final float finalX = m[Matrix.MTRANS_X] + getImageWidth() * px;
        final float finalY = m[Matrix.MTRANS_Y] + getImageHeight() * py;
        return new PointF(finalX, finalY);
    }

    /**
     * Fling launches sequential runnables which apply
     * the fling graphic to the image. The values for the translation
     * are interpolated by the Scroller.
     * @author Ortiz
     *
     */
    private class Fling implements Runnable {

        CompatScroller scroller;
        int currX, currY;

        Fling(final int velocityX, final int velocityY) {
            setState(State.FLING);
            scroller = new CompatScroller(context);
            matrix.getValues(m);

            final int startX = (int) m[Matrix.MTRANS_X];
            final int startY = (int) m[Matrix.MTRANS_Y];
            final int minX;
            final int maxX;
            final int minY;
            final int maxY;

            if (getImageWidth() > viewWidth) {
                minX = viewWidth - (int) getImageWidth();
                maxX = 0;

            } else {
                minX = maxX = startX;
            }

            if (getImageHeight() > viewHeight) {
                minY = viewHeight - (int) getImageHeight();
                maxY = 0;

            } else {
                minY = maxY = startY;
            }

            scroller.fling(startX, startY, (int) velocityX, (int) velocityY, minX,
                    maxX, minY, maxY);
            currX = startX;
            currY = startY;
        }

        public void cancelFling() {
            if (scroller != null) {
                setState(State.NONE);
                scroller.forceFinished(true);
            }
        }

        @Override
        public void run() {

            //
            // OnTouchImageViewListener is set: TouchImageView listener has been flung by user.
            // Delegate runnable updated with each frame of fling animation.
            //
            if (touchImageViewListener != null) {
                touchImageViewListener.onMove();
            }

            if (scroller.isFinished()) {
                scroller = null;
                return;
            }

            if (scroller.computeScrollOffset()) {
                final int newX = scroller.getCurrX();
                final int newY = scroller.getCurrY();
                final int transX = newX - currX;
                final int transY = newY - currY;
                currX = newX;
                currY = newY;
                matrix.postTranslate(transX, transY);
                fixTrans();
                setImageMatrix(matrix);
                compatPostOnAnimation(this);
            }
        }
    }

    @TargetApi(VERSION_CODES.GINGERBREAD)
    private static class CompatScroller {
        Scroller scroller;
        OverScroller overScroller;
        boolean isPreGingerbread;

        public CompatScroller(final Context context) {
            isPreGingerbread = false;
            overScroller = new OverScroller(context);
        }

        public void fling(final int startX, final int startY, final int velocityX, final int velocityY, final int minX, final int maxX,
                final int minY, final int maxY) {
            if (isPreGingerbread) {
                scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            } else {
                overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
            }
        }

        public void forceFinished(final boolean finished) {
            if (isPreGingerbread) {
                scroller.forceFinished(finished);
            } else {
                overScroller.forceFinished(finished);
            }
        }

        public boolean isFinished() {
            if (isPreGingerbread) {
                return scroller.isFinished();
            } else {
                return overScroller.isFinished();
            }
        }

        public boolean computeScrollOffset() {
            if (isPreGingerbread) {
                return scroller.computeScrollOffset();
            } else {
                overScroller.computeScrollOffset();
                return overScroller.computeScrollOffset();
            }
        }

        public int getCurrX() {
            if (isPreGingerbread) {
                return scroller.getCurrX();
            } else {
                return overScroller.getCurrX();
            }
        }

        public int getCurrY() {
            if (isPreGingerbread) {
                return scroller.getCurrY();
            } else {
                return overScroller.getCurrY();
            }
        }
    }

    @TargetApi(VERSION_CODES.JELLY_BEAN)
    private void compatPostOnAnimation(final Runnable runnable) {
        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
            postOnAnimation(runnable);

        } else {
            postDelayed(runnable, 1000 / 60);
        }
    }

    private static class ZoomVariables {
        public float scale;
        public float focusX;
        public float focusY;
        public ScaleType scaleType;

        public ZoomVariables(
                final float scale, final float focusX, final float focusY, final ScaleType scaleType) {
            this.scale = scale;
            this.focusX = focusX;
            this.focusY = focusY;
            this.scaleType = scaleType;
        }
    }

    private void printMatrixInfo() {
        final float[] n = new float[9];
        matrix.getValues(n);
        Log.d(DEBUG, "Scale: " + n[Matrix.MSCALE_X] + " TransX: " + n[Matrix.MTRANS_X] + " TransY: "
                + n[Matrix.MTRANS_Y]);
    }
}