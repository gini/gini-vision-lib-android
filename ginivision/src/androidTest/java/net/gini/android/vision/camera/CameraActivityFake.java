package net.gini.android.vision.camera;

import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVisionFeatureConfiguration;
import net.gini.android.vision.document.QRCodeDocument;
import net.gini.android.vision.internal.camera.api.CameraControllerFake;

/**
 * Created by Alpar Szotyori on 15.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class CameraActivityFake extends CameraActivity {

    private CameraFragmentCompatFake mCameraFragmentCompatFake;
    private QRCodeDocument mQRCodeDocument;

    @Override
    protected CameraFragmentCompat createCameraFragmentCompat() {
        return mCameraFragmentCompatFake = CameraFragmentCompatFake.createInstance();
    }

    @Override
    protected CameraFragmentCompat createCameraFragmentCompat(
            @NonNull final GiniVisionFeatureConfiguration giniVisionFeatureConfiguration) {
        return mCameraFragmentCompatFake = CameraFragmentCompatFake.createInstance(
                giniVisionFeatureConfiguration);
    }

    @Override
    public void onQRCodeAvailable(@NonNull final QRCodeDocument qrCodeDocument) {
        mQRCodeDocument = qrCodeDocument;
    }

    public CameraControllerFake getCameraControllerFake() {
        return mCameraFragmentCompatFake.getCameraControllerFake();
    }

    public CameraFragmentImplFake getCameraFragmentImplFake() {
        return mCameraFragmentCompatFake.getCameraFragmentImplFake();
    }

    public QRCodeDocument getQRCodeDocument() {
        return mQRCodeDocument;
    }
}
