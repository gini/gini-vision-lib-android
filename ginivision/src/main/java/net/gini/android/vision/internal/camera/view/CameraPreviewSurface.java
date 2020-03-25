package net.gini.android.vision.internal.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import net.gini.android.vision.internal.util.Size;

/**
 * Internal use only.
 *
 * @suppress
 */
public class CameraPreviewSurface extends SurfaceView {

    private Size mPreviewSize;

    /**
     * Internal use only.
     *
     * @suppress
     */
    public enum ScaleType {
        CENTER_RESIZE,
        CENTER_INSIDE
    }

    private ScaleType mScaleType = ScaleType.CENTER_INSIDE;

    public CameraPreviewSurface(final Context context) {
        super(context);
    }

    public CameraPreviewSurface(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreviewSurface(final Context context, final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPreviewSize(final Size previewSize) {
        mPreviewSize = previewSize;
        requestLayout();
    }

    public void setScaleType(final ScaleType scaleType) {
        mScaleType = scaleType;
        requestLayout();
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int width = getWidth();
        final int height = getHeight();

        if (width > 0 && height > 0
                && mPreviewSize != null) {
            final float aspectRatioSurface = (float) width / (float) height;
            final float aspectRatioPreview =
                    (float) mPreviewSize.width / (float) mPreviewSize.height;

            int adjustedWidth = width;
            int adjustedHeight = height;

            switch (mScaleType) {
                case CENTER_RESIZE:
                    if (aspectRatioSurface < aspectRatioPreview) {
                        // surface width < preview width AND surface height > preview height
                        // Keep the height and change the width to resize the surface to the preview's aspect ratio
                        adjustedWidth = (int) (height * aspectRatioPreview);
                    } else if (aspectRatioSurface > aspectRatioPreview) {
                        // surface width > preview width AND surface height < preview height
                        // Keep the width and change the height to resize the surface to the preview's aspect ratio
                        adjustedHeight = (int) (width / aspectRatioPreview);
                    }
                    break;
                case CENTER_INSIDE:
                    if (aspectRatioSurface < aspectRatioPreview) {
                        // surface width < preview width AND surface height > preview height
                        // Keep the width and change the height to fit the preview inside the surface's original size
                        adjustedHeight = (int) (width / aspectRatioPreview);
                    } else if (aspectRatioSurface > aspectRatioPreview) {
                        // surface width > preview width AND surface height < preview height
                        // Keep the height and change the width to fit the preview inside the surface's original size
                        adjustedWidth = (int) (height * aspectRatioPreview);
                    }
                    break;
                default:
                    break;
            }

            setMeasuredDimension(adjustedWidth, adjustedHeight);
        }
    }
}
