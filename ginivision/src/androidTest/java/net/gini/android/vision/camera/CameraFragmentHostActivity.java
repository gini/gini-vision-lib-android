package net.gini.android.vision.camera;

import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVisionError;
import net.gini.android.vision.document.GiniVisionMultiPageDocument;
import net.gini.android.vision.document.QRCodeDocument;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;

import androidx.annotation.NonNull;

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
    public void onProceedToMultiPageReviewScreen(
            @NonNull final GiniVisionMultiPageDocument multiPageDocument) {

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

    @Override
    public void onExtractionsAvailable(
            @NonNull final Map<String, GiniVisionSpecificExtraction> extractions) {

    }
}
