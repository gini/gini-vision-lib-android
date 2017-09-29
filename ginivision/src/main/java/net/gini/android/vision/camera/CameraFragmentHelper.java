package net.gini.android.vision.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.GiniVisionConfig;

class CameraFragmentHelper {

    private static final String ARGS_GINI_VISION_CONFIG = "GV_ARGS_GINI_VISION_CONFIG";

    public static Bundle createArguments(@NonNull final GiniVisionConfig giniVisionConfig) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_GINI_VISION_CONFIG, giniVisionConfig);
        return arguments;
    }

    static CameraFragmentImpl createFragmentImpl(@NonNull CameraFragmentImplCallback fragment, @Nullable Bundle arguments) {
        GiniVisionConfig giniVisionConfig = null;
        if (arguments != null) {
            giniVisionConfig = arguments.getParcelable(ARGS_GINI_VISION_CONFIG);
        }
        return new CameraFragmentImpl(fragment, giniVisionConfig);
    }

    public static void setListener(@NonNull CameraFragmentImpl fragmentImpl, @NonNull Context context) {
        if (context instanceof CameraFragmentListener) {
            fragmentImpl.setListener((CameraFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement CameraFragmentListener.");
        }
    }
}
