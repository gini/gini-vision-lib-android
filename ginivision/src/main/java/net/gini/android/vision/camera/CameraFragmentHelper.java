package net.gini.android.vision.camera;

import android.content.Context;
import android.os.Bundle;

import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.internal.ui.FragmentImplCallback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class CameraFragmentHelper {

    private static final String ARGS_GINI_VISION_FEATURES = "GV_ARGS_GINI_VISION_FEATURES";

    public static Bundle createArguments(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        final Bundle arguments = new Bundle();
        arguments.putParcelable(ARGS_GINI_VISION_FEATURES, giniVisionFeatureConfiguration);
        return arguments;
    }

    @NonNull
    CameraFragmentImpl createFragmentImpl(@NonNull final FragmentImplCallback fragment,
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
            @NonNull final FragmentImplCallback fragment,
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return new CameraFragmentImpl(fragment, giniVisionFeatureConfiguration);
    }

    @NonNull
    protected CameraFragmentImpl createCameraFragment(
            @NonNull final FragmentImplCallback fragment) {
        return new CameraFragmentImpl(fragment);
    }

    public static void setListener(@NonNull final CameraFragmentImpl fragmentImpl,
            @NonNull final Context context, @Nullable final CameraFragmentListener listener) {
        if (context instanceof CameraFragmentListener) {
            fragmentImpl.setListener((CameraFragmentListener) context);
        } else if (listener != null) {
            fragmentImpl.setListener(listener);
        } else {
            throw new IllegalStateException(
                    "CameraFragmentListener not set. "
                            + "You can set it with CameraFragment[Compat,Standard]#setListener() or "
                            + "by making the host activity implement the CameraFragmentListener.");
        }
    }
}
