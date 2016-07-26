package net.gini.android.vision.camera.api;

import android.support.annotation.NonNull;
import android.view.SurfaceHolder;
import android.view.View;

import net.gini.android.vision.camera.photo.Size;
import net.gini.android.vision.util.promise.SimplePromise;

/**
 * <p>
 *     An interface which defines an API for the camera used with the Gini Vision Library.
 * </p>
 * <p>
 *     We use this interface with the deprecated Camera API and the new Camera2 API to publish a common API for the required
 *     camera features.
 * </p>
 * @exclude
 */
public interface CameraInterface {
    /**
     * <p>
     *     Opens the first back-facing camera.
     * </p>
     * @return a promise
     */
    @NonNull
    SimplePromise open();

    /**
     * <p>
     *     Closes the camera.
     * </p>
     */
    void close();

    /**
     * <p>
     *     Starts the preview using the given {@link SurfaceHolder}.
     * </p>
     * <p>
     *     <b>Note</b>: the {@link android.view.SurfaceView} must have been created when starting the preview.
     * </p>
     * @param surfaceHolder the {@link SurfaceHolder} for the camera preview {@link android.view.SurfaceView}
     * @return a promise
     */
    @NonNull
    SimplePromise startPreview(@NonNull SurfaceHolder surfaceHolder);

    /**
     * Stops the camera preview.
     */
    void stopPreview();

    /**
     * <p>
     *     Enables tap-to-focus using the given view by adding touch handling to it and transforming the touch point coordinates
     *     to the camera sensor's coordinate system.
     * </p>
     * <p>
     *     <b>Note</b>: the view should have the same size as the camera preview and be above it. You could also set the
     *     camera preview {@link android.view.SurfaceView} directly as the tap view..
     * </p>
     * @param tapView the view used to handle taps
     */
    void enableTapToFocus(@NonNull View tapView);

    /**
     * Disables tap-to-focus.
     * @param tapView the view set with {@link CameraInterface#enableTapToFocus(View)} to handle taps
     */
    void disableTapToFocus(@NonNull View tapView);

    /**
     * <p>
     *     Starts a focus run.
     * </p>
     * @return a promise
     */
    @NonNull
    SimplePromise focus();

    /**
     * <p>
     *     Take a picture with the camera.
     * </p>
     * @return a promise
     */
    @NonNull
    SimplePromise takePicture();

    /**
     * <p>
     *     The selected preview size for the camera. It is the largest preview size which has an aspect ratio of 4:3.
     * </p>
     * @return preview size
     */
    @NonNull
    Size getPreviewSize();

    /**
     *<p>
     *     The selected picture size for the camera. It is the largest picture size which has an aspect ratio of 4:3.
     *</p>
     * @return picture size
     */
    @NonNull
    Size getPictureSize();
}
