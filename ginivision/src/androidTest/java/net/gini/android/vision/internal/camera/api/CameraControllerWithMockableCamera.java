package net.gini.android.vision.internal.camera.api;

import android.app.Activity;
import android.hardware.Camera;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class CameraControllerWithMockableCamera extends CameraController {

    private Camera mMockCamera;

    public CameraControllerWithMockableCamera(@NonNull Activity activity) {
        super(activity);
    }

    public void setMockCamera(Camera mockCamera) {
        this.mMockCamera = mockCamera;
    }

    @Nullable
    @Override
    protected Camera openCamera() {
        if (mMockCamera != null) {
            return mMockCamera;
        }
        return super.openCamera();
    }
}
