package net.gini.android.vision.camera;

import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.internal.camera.api.CameraControllerFake;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 15.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraFragmentCompatFake extends CameraFragmentCompat {

    private CameraFragmentImplFake mCameraFragmentImplFake;

    public static CameraFragmentCompatFake createInstance() {
        return new CameraFragmentCompatFake();
    }

    public static CameraFragmentCompatFake createInstance(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        final CameraFragmentCompatFake fragment = new CameraFragmentCompatFake();
        fragment.setArguments(
                CameraFragmentHelper.createArguments(giniVisionFeatureConfiguration));
        return fragment;
    }

    @Override
    protected CameraFragmentImpl createFragmentImpl() {
        final CameraFragmentHelperFake cameraFragmentHelperFake = new CameraFragmentHelperFake();
        final CameraFragmentImpl cameraFragmentImpl = cameraFragmentHelperFake.createFragmentImpl(
                this, getArguments());
        mCameraFragmentImplFake = cameraFragmentHelperFake.getCameraFragmentImplFake();
        return cameraFragmentImpl;
    }

    public CameraControllerFake getCameraControllerFake() {
        return mCameraFragmentImplFake.getCameraControllerFake();
    }

    public CameraFragmentImplFake getCameraFragmentImplFake() {
        return mCameraFragmentImplFake;
    }
}
