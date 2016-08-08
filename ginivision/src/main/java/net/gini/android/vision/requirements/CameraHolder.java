package net.gini.android.vision.requirements;

import android.hardware.Camera;
import android.support.annotation.Nullable;

class CameraHolder {

    private Camera mCamera;

    public void openCamera() throws RuntimeException {
        mCamera = Camera.open();
    }

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Nullable
    public Camera getCamera() {
        return mCamera;
    }
}
