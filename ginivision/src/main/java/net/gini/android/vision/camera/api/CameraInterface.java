package net.gini.android.vision.camera.api;

import android.view.SurfaceHolder;
import android.view.View;

import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.camera.photo.Size;

public interface CameraInterface {
    void open();
    void close();
    void startPreview(SurfaceHolder surfaceHolder);
    void stopPreview();
    void enableTapToFocus(View tapView);
    void disableTapToFocus(View tapView);
    void focus();
    void takePicture();
    void setListener(Listener listener);
    Size getPreviewSize();
    Size getPictureSize();

    interface Listener {
        void onCameraOpened();

        void onCameraClosed();

        void onCameraFocusFinished(boolean success);

        void onPhotoTaken(Photo photo);

        void onCameraError(RuntimeException e);
    }
}
