package net.gini.android.vision.requirements;

import android.hardware.Camera;

import androidx.annotation.Nullable;

class CameraHolder {

    private Camera mCamera;

    public void closeCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null; // NOPMD
        }
    }

    public boolean hasCamera() throws RuntimeException {
        openCamera();
        return mCamera != null;
    }

    @Nullable
    public Camera.Parameters getCameraParameters() throws RuntimeException {
        openCamera();
        if (mCamera != null) {
            return mCamera.getParameters();
        }
        return null;
    }

    private void openCamera() throws RuntimeException {
        if (mCamera == null) {
            mCamera = Camera.open();
        }
    }
}
