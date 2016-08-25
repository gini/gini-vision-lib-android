package net.gini.android.vision.internal.camera.api;

import static net.gini.android.vision.internal.camera.api.Util.getLargestFourThreeRatioSize;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Camera;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import jersey.repackaged.jsr166e.CompletableFuture;

/**
 * @exclude
 */
public class CameraController implements CameraInterface {

    private static final Logger LOG = LoggerFactory.getLogger(CameraController.class);

    private Camera mCamera;

    private boolean mPreviewRunning = false;
    private AtomicReference<CompletableFuture<Boolean>> mFocusingFuture = new AtomicReference<>();
    private AtomicReference<CompletableFuture<Photo>> mTakingPictureFuture = new AtomicReference<>();

    private Size mPreviewSize = new Size(0, 0);
    private Size mPictureSize = new Size(0, 0);

    private final Activity mActivity;
    private final Handler mResetFocusHandler;

    private Runnable mResetFocusMode = new Runnable() {
        @Override
        public void run() {
            if (mCamera == null) {
                return;
            }
            Camera.Parameters parameters = mCamera.getParameters();
            if (!parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCamera.setParameters(parameters);
        }
    };

    public CameraController(@NonNull Activity activity) {
        mActivity = activity;
        mResetFocusHandler = new Handler();
    }

    @NonNull
    @Override
    public CompletableFuture<Void> open() {
        LOG.info("Open camera");
        if (mCamera != null) {
            LOG.debug("Camera already open");
            LOG.info("Camera opened");
            return CompletableFuture.completedFuture(null);
        }
        try {
            mCamera = Camera.open();
            if (mCamera != null) {
                configureCamera(mActivity);
                LOG.info("Camera opened");
                return CompletableFuture.completedFuture(null);
            } else {
                LOG.error("No back-facing camera");
                return failedFuture(new CameraException("No back-facing camera"));
            }
        } catch (RuntimeException e) {
            LOG.error("Cannot start camera", e);
            return failedFuture(e);
        }
    }

    @Override
    public void close() {
        LOG.info("Closing camera");
        if (mCamera == null) {
            LOG.debug("Camera already closed");
            LOG.info("Camera closed");
            return;
        }
        mCamera.release();
        mCamera = null;
        LOG.info("Camera closed");
    }

    @NonNull
    @Override
    public CompletableFuture<Void> startPreview(@NonNull SurfaceHolder surfaceHolder) {
        LOG.info("Start preview for the given SurfaceHolder");
        if (mCamera == null) {
            LOG.error("Cannot start preview: camera not open");
            return failedFuture(new CameraException("Cannot start preview: camera not open"));
        }
        if (mPreviewRunning) {
            LOG.info("Preview already running");
            return CompletableFuture.completedFuture(null);
        }
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
            mPreviewRunning = true;
            LOG.info("Preview started");
        } catch (IOException e) {
            LOG.error("Cannot start preview", e);
            return failedFuture(e);
        }
        return CompletableFuture.completedFuture(null);
    }

    @NonNull
    @Override
    public CompletableFuture<Void> startPreview() {
        LOG.info("Start preview");
        if (mCamera == null) {
            LOG.error("Cannot start preview: camera not open");
            return failedFuture(new CameraException("Cannot start preview: camera not open"));
        }
        if (mPreviewRunning) {
            LOG.info("Preview already running");
            return CompletableFuture.completedFuture(null);
        }
        mCamera.startPreview();
        mPreviewRunning = true;
        LOG.info("Preview started");
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public void stopPreview() {
        LOG.info("Stop preview");
        if (mCamera == null) {
            LOG.info("Preview not running: camera is stopped");
            return;
        }
        mCamera.stopPreview();
        mPreviewRunning = false;
        LOG.info("Preview stopped");
    }

    @Override
    public boolean isPreviewRunning() {
        return mPreviewRunning;
    }

    @Override
    public void enableTapToFocus(@NonNull View tapView, @Nullable final TapToFocusListener listener) {
        LOG.info("Tap to focus enabled");
        tapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    final float x = event.getX();
                    final float y = event.getY();
                    LOG.debug("Handling tap to focus touch at point ({}, {})", x, y);
                    if (mCamera == null) {
                        LOG.error("Cannot focus on tap: camera not open");
                        return false;
                    }
                    final CompletableFuture<Boolean> focused = new CompletableFuture<>();
                    do {
                        // Checking whether a completable is already available in which case focusing is in progress
                        final CompletableFuture<Boolean> inProgress = mFocusingFuture.get();
                        if (inProgress != null) {
                            LOG.info("Already focusing");
                            return false;
                        }
                        // We rerun the above in case a completable was set by another thread
                        // Otherwise we set the new completable and exit the loop
                    } while (!mFocusingFuture.compareAndSet(null, focused));

                    mCamera.cancelAutoFocus();
                    Rect focusRect = calculateTapArea(x, y, getBackFacingCameraOrientation(), view.getWidth(), view.getHeight());
                    LOG.debug("Focus rect calculated (l:{}, t:{}, r:{}, b:{})", focusRect.left, focusRect.top, focusRect.right, focusRect.bottom);

                    Camera.Parameters parameters = mCamera.getParameters();
                    if (!parameters.getFocusMode().equals(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    }
                    if (parameters.getMaxNumFocusAreas() > 0) {
                        List<Camera.Area> mylist = new ArrayList<Camera.Area>();
                        mylist.add(new Camera.Area(focusRect, 1000));
                        parameters.setFocusAreas(mylist);
                        LOG.debug("Focus area set");
                    } else {
                        LOG.warn("Focus areas not supported");
                    }

                    try {
                        if (listener != null) {
                            listener.onFocusing(new Point(Math.round(x), Math.round(y)));
                        }
                        mCamera.setParameters(parameters);
                        LOG.info("Focusing started");
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(final boolean success, Camera camera) {
                                LOG.info("Focusing finished with result: {}", success);
                                mFocusingFuture.set(null);
                                focused.complete(success);
                                if (listener != null) {
                                    listener.onFocused(success);
                                }
                                mResetFocusHandler.removeCallbacks(mResetFocusMode);
                                mResetFocusHandler.postDelayed(mResetFocusMode, 5000);
                            }
                        });
                    } catch (Exception e) {
                        mFocusingFuture.set(null);
                        LOG.error("Could not focus", e);
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void disableTapToFocus(@NonNull View tapView) {
        LOG.info("Tap to focus disabled");
        tapView.setOnTouchListener(null);
    }

    @NonNull
    @Override
    public CompletableFuture<Boolean> focus() {
        LOG.info("Start focusing");

        if (mCamera == null) {
            LOG.error("Cannot focus: camera not open");
            return CompletableFuture.completedFuture(false);
        }

        final CompletableFuture<Boolean> completed = new CompletableFuture<>();
        do {
            // Checking whether a completable is already available in which case focusing is in progress
            final CompletableFuture<Boolean> inProgress = mFocusingFuture.get();
            if (inProgress != null) {
                LOG.info("Already focusing");
                return inProgress;
            }
            // We rerun the above in case a completable was set by another thread
            // Otherwise we set the new completable and exit the loop
        } while (!mFocusingFuture.compareAndSet(null, completed));

        mCamera.cancelAutoFocus();
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(final boolean success, Camera camera) {
                LOG.info("Focusing finished with result: {}", success);
                mFocusingFuture.set(null);
                completed.complete(success);
            }
        });

        return completed;
    }

    @NonNull
    @Override
    public CompletableFuture<Photo> takePicture() {
        LOG.info("Take picture");

        if (mCamera == null) {
            LOG.error("Cannot take picture: camera not open");
            return failedFuture(new CameraException("Cannot take picture: camera not open"));
        }

        final CompletableFuture<Photo> pictureTaken = new CompletableFuture<>();
        do {
            // Checking whether a completable is already available in which case taking the picture is in progress
            final CompletableFuture<Photo> inProgress = mTakingPictureFuture.get();
            if (inProgress != null) {
                LOG.info("Already taking a picture");
                return inProgress;
            }
            // We rerun the above in case a completable was set by another thread
            // Otherwise we set the new completable and exit the loop
        } while (!mTakingPictureFuture.compareAndSet(null, pictureTaken));

        // Preview is stopped after the picture was taken, but for it's sufficient to declare preview
        // as being stopped before it is really stopped
        mPreviewRunning = false;

        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] bytes, Camera camera) {
                mTakingPictureFuture.set(null);
                final Photo photo = Photo.fromJpeg(bytes, getBackFacingCameraOrientation());
                LOG.info("Picture taken");
                pictureTaken.complete(photo);
            }
        });
        return pictureTaken;
    }

    @NonNull
    @Override
    public Size getPreviewSize() {
        return mPreviewSize;
    }

    @NonNull
    @Override
    public Size getPictureSize() {
        return mPictureSize;
    }

    private void configureCamera(Activity activity) {
        LOG.debug("Configuring camera");
        if (mCamera == null) {
            LOG.error("Cannot configure camera: camera not open");
            return;
        }

        Camera.Parameters params = mCamera.getParameters();

        List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
        Size previewSize = getLargestFourThreeRatioSize(previewSizes);
        if (previewSize != null) {
            mPreviewSize = previewSize;
            params.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            LOG.debug("Preview size ({}, {})", mPreviewSize.width, mPreviewSize.height);
        } else {
            LOG.warn("No 4:3 preview size found");
        }

        List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
        Size pictureSize = getLargestFourThreeRatioSize(pictureSizes);
        if (pictureSize != null) {
            mPictureSize = pictureSize;
            params.setPictureSize(mPictureSize.width, mPictureSize.height);
            LOG.debug("Picture size ({}, {})", mPictureSize.width, mPictureSize.height);
        } else {
            LOG.warn("No 4:3 picture size found");
        }

        if (params.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            LOG.debug("Focus mode continuous picture");
        } else {
            LOG.warn("Focus mode continuous picture not supported");
        }

        List<String> supportedFlashModes = params.getSupportedFlashModes();
        if (supportedFlashModes != null && supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            LOG.debug("Flash on");
        } else {
            LOG.warn("Flash not supported");
        }

        mCamera.setParameters(params);

        setCameraDisplayOrientation(activity, mCamera);
    }

    private void setCameraDisplayOrientation(Activity activity, android.hardware.Camera camera) {
        LOG.debug("Setting camera display orientation");
        Camera.CameraInfo info = getBackFacingCameraInfo();
        if (info == null) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        LOG.debug("Default display rotation {}", degrees);

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        LOG.debug("Camera display orientation set to {}", result);
    }

    @Nullable
    private Camera.CameraInfo getBackFacingCameraInfo() {
        LOG.debug("Getting back facing camera info");
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                LOG.debug("Camera info found");
                return cameraInfo;
            }
        }
        LOG.debug("No camera info found");
        return null;
    }

    private int getBackFacingCameraOrientation() {
        LOG.debug("Getting back facing camera orientation");
        Camera.CameraInfo cameraInfo = getBackFacingCameraInfo();
        if (cameraInfo != null) {
            LOG.debug("Camera orientation: {}", cameraInfo.orientation);
            return cameraInfo.orientation;
        }
        LOG.debug("No camera info, using default camera orientation: 0");
        return 0;
    }

    /**
     * <p>
     * Converts the tap's coordinates in the view to the coordinates used by the camera sensor.
     * </p>
     * <p>
     * The camera sensor's coordinates are (0,0) in the center, (-1000,-1000) in the top left and
     * (1000,1000) in the lower right:
     * <pre>
     * (-1000,-1000)-----|-----(1000,-1000)
     * |                 |                |
     * |                 |                |
     * ----------------(0,0)---------------
     * |                 |                |
     * |                 |                |
     * (-1000,1000)------|------(1000,1000)
     * </pre>
     * The sensor's coordinates are not adapted to the display orientation, that means that in our case where
     * we always show the camera preview in portrait, the coordinates are simply turned 90 degrees clockwise
     * (for most devices, for others the calculated rect is rotated):
     * <pre>
     * (-1000,1000)---|---(-1000,-1000)
     * |              |               |
     * |              |               |
     * |              |               |
     * |      A       |       B       |
     * |              |               |
     * |              |               |
     * |              |               |
     * |              |               |
     * |              |               |
     * -------------(0,0)--------------
     * |              |               |
     * |              |               |
     * |              |               |
     * |      C       |       D       |
     * |              |               |
     * |              |               |
     * |              |               |
     * |              |               |
     * |              |               |
     * (1000,1000)----|----(1000,-1000)
     * </pre>
     * </p>
     * <p>
     * For easier conversion, we divided the view area into four parts (A, B, C, D) and do the conversion for each one
     * separately.
     * </p>
     * <p>
     * Calculations are made with the assumption of a 90 degree
     * camera orientation. The real camera's orientation is normalized by subtracting 90 degrees and then the
     * calculated
     * rect is rotated by the normalized degrees.
     * </p>
     *
     * @param x             tap's X position in the view
     * @param y             tap's Y position in the view
     * @param orientation   camera's orientation, see {@link Camera.CameraInfo#orientation}
     * @param tapViewWidth  the width of the tappable view
     * @param tapViewHeight the height of the tappable view
     */
    private Rect calculateTapArea(float x, float y, int orientation, int tapViewWidth, int tapViewHeight) {
        Rect rect = new Rect(0, 0, 0, 0);
        if (x < tapViewWidth / 2.f && y < tapViewHeight / 2.f) {
            // A: x: -1000 .. 0; y: 1000 .. 0
            rect.left = -(1000 - (int) (1000 * (y / (tapViewHeight / 2.f))));
            rect.top = 1000 - (int) (1000 * (x / (tapViewWidth / 2.f)));
        } else if (x < tapViewWidth / 2.f && y >= tapViewHeight / 2.f) {
            // C: x: 0 .. 1000; y: 1000 .. 0
            y = y - tapViewHeight / 2.f;
            rect.left = (int) (1000 * (y / (tapViewHeight / 2.f)));
            rect.top = 1000 - (int) (1000 * (x / (tapViewWidth / 2.f)));
        } else if (x >= tapViewWidth / 2.f && y < tapViewHeight / 2.f) {
            // B: x: -1000 .. 0; y: 0 .. -1000
            x = x - tapViewWidth / 2.f;
            rect.left = -(1000 - (int) (1000 * (y / (tapViewHeight / 2.f))));
            rect.top = -(int) (1000 * (x / (tapViewWidth / 2.f)));
        } else if (x >= tapViewWidth / 2.f && y >= tapViewHeight / 2.f) {
            // D: x: 0 .. 1000; y: 0 .. -1000
            x = x - tapViewWidth / 2.f;
            y = y - tapViewHeight / 2.f;
            rect.left = (int) (1000 * (y / (tapViewHeight / 2.f)));
            rect.top = -(int) (1000 * (x / (tapViewWidth / 2.f)));
        }
        // Give a size to the rect
        rect.bottom = rect.top + 5;
        rect.right = rect.left + 5;
        // Rotate the rect according to the camera's orientation
        // Tap area was calculated for a camera with a 90 degrees orientation
        // so we have to normalize the rotation taking that into account
        int rectRotation = orientation - 90;
        RectF rectF = new RectF(rect);
        Matrix matrix = new Matrix();
        matrix.setRotate(rectRotation);
        matrix.mapRect(rectF);
        rect.set((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
        return rect;
    }

    private static <T> CompletableFuture<T> failedFuture(final Throwable throwable) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        future.completeExceptionally(throwable);
        return future;
    }
}
