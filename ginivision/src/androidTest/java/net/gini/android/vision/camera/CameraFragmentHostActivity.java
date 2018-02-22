package net.gini.android.vision.camera;

import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.document.QRCodeDocument;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class CameraFragmentHostActivity extends CameraFragmentHostActivityNotListener implements
        CameraFragmentListener {

    private boolean hasDocument = false;

    public boolean hasDocument() {
        return hasDocument;
    }

    @Override
    public void onDocumentAvailable(@NonNull final Document document) {
        hasDocument = true;
    }

    @Override
    public void onQRCodeAvailable(@NonNull final QRCodeDocument qrCodeDocument) {

    }

    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {

    }

    @Override
    public void onError(@NonNull final GiniVisionError error) {

    }
}
