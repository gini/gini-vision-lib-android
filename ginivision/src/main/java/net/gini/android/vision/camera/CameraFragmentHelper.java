package net.gini.android.vision.camera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.GiniVisionFeatureConfiguration;

class CameraFragmentHelper {

    private static final String ARGS_GINI_VISION_FEATURES = "GV_ARGS_GINI_VISION_FEATURES";

    public static Bundle createArguments(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_GINI_VISION_FEATURES, giniVisionFeatureConfiguration);
        return arguments;
    }

    static CameraFragmentImpl createFragmentImpl(@NonNull CameraFragmentImplCallback fragment,
            @Nullable Bundle arguments) {
        if (arguments != null) {
            final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
                    arguments.getParcelable(ARGS_GINI_VISION_FEATURES);
            if (giniVisionFeatureConfiguration != null) {
                return new CameraFragmentImpl(fragment, giniVisionFeatureConfiguration);
            }
        }
        return new CameraFragmentImpl(fragment);
    }

    public static void setListener(@NonNull CameraFragmentImpl fragmentImpl, @NonNull Context context) {
        if (context instanceof CameraFragmentListener) {
            fragmentImpl.setListener((CameraFragmentListener) context);
        } else {
            throw new IllegalStateException("Hosting activity must implement CameraFragmentListener.");
        }
    }
}
