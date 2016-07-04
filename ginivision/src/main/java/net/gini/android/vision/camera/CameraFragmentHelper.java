package net.gini.android.vision.camera;

import android.content.Context;

class CameraFragmentHelper {

    public static void setListener(CameraFragmentImpl fragmentImpl, Context context) {
        if (context instanceof CameraFragmentListener) {
            fragmentImpl.setListener((CameraFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement CameraFragmentListener.");
        }
    }
}
