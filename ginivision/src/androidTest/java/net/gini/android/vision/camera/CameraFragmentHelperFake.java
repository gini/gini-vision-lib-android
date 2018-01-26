package net.gini.android.vision.camera;

import static org.mockito.Mockito.spy;

import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionFeatureConfiguration;

/**
 * Created by Alpar Szotyori on 15.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraFragmentHelperFake extends CameraFragmentHelper {

    private CameraFragmentImplFake mCameraFragmentImplFake;

    @NonNull
    @Override
    protected CameraFragmentImpl createCameraFragment(
            @NonNull final CameraFragmentImplCallback fragment,
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return mCameraFragmentImplFake = spy(new CameraFragmentImplFake(fragment,
                giniVisionFeatureConfiguration));
    }

    @NonNull
    @Override
    protected CameraFragmentImpl createCameraFragment(
            @NonNull final CameraFragmentImplCallback fragment) {
        return mCameraFragmentImplFake = spy(new CameraFragmentImplFake(fragment));
    }

    CameraFragmentImplFake getCameraFragmentImplFake() {
        return mCameraFragmentImplFake;
    }
}
