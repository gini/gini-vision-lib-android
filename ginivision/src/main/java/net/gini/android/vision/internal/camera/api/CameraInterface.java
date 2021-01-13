package net.gini.android.vision.internal.camera.api;

import android.graphics.Point;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.View;

import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * Internal use only.
 *
 * <p>
 *     An interface which defines an API for the camera used with the Gini Vision Library.
 * </p>
 * <p>
 *     We use this interface with the deprecated Camera API and the new Camera2 API to publish a common API for the required
 *     camera features.
 * </p>
 *
 * @suppress
 */
public interface CameraInterface {
    /**
     * <p>
     *     Opens the first back-facing camera.
     * </p>
     * @return a {@link CompletableFuture} that completes when the camera was opened
     */
    @NonNull
    CompletableFuture<Void> open();

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
     * @return a {@link CompletableFuture} that completes when the preview was started
     */
    @NonNull
    CompletableFuture<Void> startPreview(@NonNull SurfaceHolder surfaceHolder);


    /**
     * <p>
     *     Starts the preview using the {@link SurfaceHolder} provided by {@link CameraInterface#startPreview(SurfaceHolder)}.
     * </p>
     * <p>
     *     This method has no effect, if no {@link android.view.SurfaceHolder} is available.
     * </p>
     */
    @NonNull
    CompletableFuture<Void> startPreview();

    /**
     * <p>
     *     Stops the camera preview.
     * </p>
     */
    void stopPreview();

    /**
     * <p>
     *     Get the state of the preview.
     * </p>
     * @return {@code true}, if the preview is running
     */
    boolean isPreviewRunning();

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
    void enableTapToFocus(@NonNull View tapView, @Nullable TapToFocusListener listener);

    /**
     * Disables tap-to-focus.
     * @param tapView the view set with {@link CameraInterface#enableTapToFocus(View, TapToFocusListener)} to handle taps
     */
    void disableTapToFocus(@NonNull View tapView);

    /**
     * <p>
     *     Start a focus run.
     * </p>
     * @return a {@link CompletableFuture} that completes with the result of the focus operation
     */
    @NonNull
    CompletableFuture<Boolean> focus();

    /**
     * <p>
     *     Take a picture with the camera.
     * </p>
     * @return a {@link CompletableFuture} that completes with the {@link Photo} object taken
     */
    @NonNull
    CompletableFuture<Photo> takePicture();

    /**
     * <p>
     *     The selected preview size for the camera. It is the largest preview size which has an aspect ratio of 4:3.
     * </p>
     * @return preview size
     */
    @NonNull
    Size getPreviewSize();

    /**
     * <p>
     *     The selected preview size for the camera rotated to match the camera orientation.
     *     It is the largest preview size which has an aspect ratio of 4:3.
     * </p>
     * @return preview size
     */
    @NonNull
    Size getPreviewSizeForDisplay();

    /**
     *<p>
     *     The selected picture size for the camera. It is the largest picture size which has an aspect ratio of 4:3.
     *</p>
     * @return picture size
     */
    @NonNull
    Size getPictureSize();

    /**
     * <p>
     *      Set a callback to recieve preview images from the camera.
     * </p>
     * @param previewCallback callback implementation
     */
    void setPreviewCallback(@NonNull Camera.PreviewCallback previewCallback);

    /**
     * <p>
     *     The rotation in degrees of the camera. Derived from the camera sensor orientation
     *     and device orientation.
     * </p>
     * @return rotation in degrees
     */
    int getCameraRotation();

    boolean isFlashAvailable();

    boolean isFlashEnabled();

    void setFlashEnabled(final boolean enabled);

    /**
     * Listener for tap to focus.
     */
    interface TapToFocusListener {
        void onFocusing(Point point);

        void onFocused(boolean success);
    }
}
