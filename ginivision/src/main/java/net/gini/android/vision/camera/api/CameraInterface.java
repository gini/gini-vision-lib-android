package net.gini.android.vision.camera.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import android.view.View;

import net.gini.android.vision.camera.photo.Photo;
import net.gini.android.vision.camera.photo.Size;

public interface CameraInterface {
    void open();
    void close();
    void startPreview(@NonNull SurfaceHolder surfaceHolder);
    void stopPreview();
    void enableTapToFocus(@NonNull View tapView);
    void disableTapToFocus(@NonNull View tapView);
    void focus();
    void takePicture();
    void setListener(@Nullable Listener listener);
    @NonNull
    Size getPreviewSize();
    @NonNull
    Size getPictureSize();

    interface Listener {
        void onCameraOpened();

        void onCameraClosed();

        void onCameraFocusFinished(boolean success);

        void onPhotoTaken(@NonNull Photo photo);

        void onCameraError(@NonNull RuntimeException e);
    }
}
