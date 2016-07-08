package net.gini.android.vision.camera.api;

import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.View;

import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.camera.photo.Size;

public class CameraController implements CameraInterface {

    private static final CameraInterface.Listener NO_OP_LISTENER = new CameraInterface.Listener() {
        @Override
        public void onCameraOpened() {
        }

        @Override
        public void onCameraClosed() {
        }

        @Override
        public void onCameraFocusFinished(boolean success) {
        }

        @Override
        public void onPhotoTaken(Photo photo) {
        }

        @Override
        public void onCameraError(RuntimeException e) {
        }
    };

    private CameraInterface.Listener mListener = NO_OP_LISTENER;

    @Override
    public void open() {
        // In the library stub we just try to open the camera and if there was an error show the missing permission
        // error view or give back the error in the result, if the error wasn't related to the missing camera permission
        try {
            Camera camera = Camera.open();
            mListener.onCameraOpened();
            if (camera != null) {
                camera.release();
            }
        } catch (RuntimeException e) {
            mListener.onCameraError(e);
        }
    }

    @Override
    public void close() {

    }

    @Override
    public void startPreview(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void stopPreview() {

    }

    @Override
    public void enableTapToFocus(View tapView) {

    }

    @Override
    public void disableTapToFocus(View tapView) {

    }

    @Override
    public void focus() {

    }

    @Override
    public void takePicture() {

    }

    @Override
    public void setListener(Listener listener) {
        if (listener == null) {
            mListener = NO_OP_LISTENER;
        } else {
            mListener = listener;
        }
    }

    @Override
    public Size getPreviewSize() {
        return null;
    }

    @Override
    public Size getPictureSize() {
        return null;
    }
}
