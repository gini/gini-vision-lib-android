package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;

import java.util.IllegalFormatException;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

class PaymentQRCodeParser implements QRCodeParser<PaymentData> {

    @NonNull
    @Override
    public PaymentData parse(@NonNull final QRCode qrCode) throws IllegalFormatException {
        return new PaymentData(qrCode.getContent(), null, null, null, null);
    }
}
