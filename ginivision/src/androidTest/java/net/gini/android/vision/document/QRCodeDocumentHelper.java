package net.gini.android.vision.document;

import android.support.annotation.NonNull;

import net.gini.android.vision.PaymentData;

/**
 * Created by Alpar Szotyori on 12.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
public class QRCodeDocumentHelper {

    public static PaymentData getPaymentData(@NonNull final QRCodeDocument qrCodeDocument) {
        return qrCodeDocument.getPaymentData();
    }

    private QRCodeDocumentHelper() {
    }
}