package net.gini.android.vision.camera;

import android.app.Activity;
import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.internal.camera.api.CameraControllerFake;
import net.gini.android.vision.internal.camera.api.CameraInterface;

/**
 * Created by Alpar Szotyori on 15.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraFragmentImplFake extends CameraFragmentImpl {

    private CameraControllerFake mCameraControllerFake;
    private int mHidePaymentDataDetectedPopupDelayMs = 10000;

    CameraFragmentImplFake(
            @NonNull final CameraFragmentImplCallback fragment) {
        super(fragment);
    }

    CameraFragmentImplFake(
            @NonNull final CameraFragmentImplCallback fragment,
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        super(fragment, giniVisionFeatureConfiguration);
    }

    @Override
    long getHideQRCodeDetectedPopupDelayMs() {
        return CameraFragmentImpl.DEFAULT_ANIMATION_DURATION + mHidePaymentDataDetectedPopupDelayMs;
    }

    void setHidePaymentDataDetectedPopupDelayMs(
            final int hidePaymentDataDetectedPopupDelayMs) {
        mHidePaymentDataDetectedPopupDelayMs = hidePaymentDataDetectedPopupDelayMs;
    }

    @Override
    long getDifferentQRCodeDetectedPopupDelayMs() {
        return 100;
    }

    @NonNull
    @Override
    protected CameraInterface createCameraController(final Activity activity) {
        return mCameraControllerFake = new CameraControllerFake();
    }

    CameraControllerFake getCameraControllerFake() {
        return mCameraControllerFake;
    }
}
