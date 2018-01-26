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
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_GINI_VISION_FEATURES, giniVisionFeatureConfiguration);
        return arguments;
    }

    @NonNull
    CameraFragmentImpl createFragmentImpl(@NonNull final CameraFragmentImplCallback fragment,
            @Nullable final Bundle arguments) {
        if (arguments != null) {
            final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration =
                    arguments.getParcelable(ARGS_GINI_VISION_FEATURES);
            if (giniVisionFeatureConfiguration != null) {
                return createCameraFragment(fragment, giniVisionFeatureConfiguration);
            }
        }
        return createCameraFragment(fragment);
    }

    @NonNull
    protected CameraFragmentImpl createCameraFragment(
            @NonNull final CameraFragmentImplCallback fragment,
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return new CameraFragmentImpl(fragment, giniVisionFeatureConfiguration);
    }

    @NonNull
    protected CameraFragmentImpl createCameraFragment(
            @NonNull final CameraFragmentImplCallback fragment) {
        return new CameraFragmentImpl(fragment);
    }

    public static void setListener(@NonNull final CameraFragmentImpl fragmentImpl,
            @NonNull final Context context) {
        if (context instanceof CameraFragmentListener) {
            fragmentImpl.setListener((CameraFragmentListener) context);
        } else {
            throw new IllegalStateException(
                    "Hosting activity must implement CameraFragmentListener.");
        }
    }
}
