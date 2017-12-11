package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;

import net.gini.android.vision.PaymentData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

class PaymentQRCodeParser implements QRCodeParser<PaymentData> {

    private final List<QRCodeParser<PaymentData>> mParsers;

    PaymentQRCodeParser() {
        mParsers = new ArrayList<>(3);
        mParsers.add(new BezahlCodeParser());
        mParsers.add(new EPC069_12Parser());
    }

    @NonNull
    @Override
    public PaymentData parse(@NonNull final String qrCodeContent)
            throws IllegalArgumentException {
        for (final QRCodeParser<PaymentData> parser : mParsers) {
            try {
                return parser.parse(qrCodeContent);
            } catch (final IllegalArgumentException ignore) {
            }
        }
        throw new IllegalArgumentException("Unknown QRCode content format.");
    }
}
