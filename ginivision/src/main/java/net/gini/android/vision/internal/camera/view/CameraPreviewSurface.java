package net.gini.android.vision.internal.camera.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

import net.gini.android.vision.internal.camera.photo.Size;

/**
 * @exclude
 */
public class CameraPreviewSurface extends SurfaceView {

    private Size mPreviewSize;

    public enum ScaleType {
        CENTER_RESIZE, CENTER_INSIDE;
    }

    private ScaleType mScaleType = ScaleType.CENTER_INSIDE;

    public CameraPreviewSurface(Context context) {
        super(context);
    }

    public CameraPreviewSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraPreviewSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setPreviewSize(Size previewSize) {
        this.mPreviewSize = previewSize;
        requestLayout();
    }

    public void setScaleType(ScaleType scaleType) {
        mScaleType = scaleType;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getWidth();
        int height = getHeight();

        if (width > 0 && height > 0 &&
                mPreviewSize != null) {
            float aspectRatioSurface = (float) width / (float) height;
            // Preview size is in landscape, we need it in portrait, switching height and width
            float aspectRatioPreview = (float) mPreviewSize.height / (float) mPreviewSize.width;

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
            }

            setMeasuredDimension(adjustedWidth, adjustedHeight);
        }
    }
}
