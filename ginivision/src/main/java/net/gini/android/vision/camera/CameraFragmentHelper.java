package net.gini.android.vision.camera;

import android.content.Context;
import android.support.annotation.NonNull;

class CameraFragmentHelper {

    public static void setListener(@NonNull CameraFragmentImpl fragmentImpl, @NonNull Context context) {
        if (context instanceof CameraFragmentListener) {
            fragmentImpl.setListener((CameraFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement CameraFragmentListener.");
        }
    }
}
