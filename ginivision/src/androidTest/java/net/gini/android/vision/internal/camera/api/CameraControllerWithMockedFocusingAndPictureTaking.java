package net.gini.android.vision.internal.camera.api;

import android.app.Activity;
import android.hardware.Camera;
import android.support.annotation.NonNull;

import jersey.repackaged.jsr166e.CompletableFuture;

public class CameraControllerWithMockedFocusingAndPictureTaking extends
        CameraControllerWithMockableCamera {

    public CameraControllerWithMockedFocusingAndPictureTaking(@NonNull final Activity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public CompletableFuture<Boolean> focus() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        future.complete(true);
        return future;
    }

    @Override
    protected void takePicture(final Camera.PictureCallback callback) {
        callback.onPictureTaken(new byte[]{0}, getCamera());
    }
}
