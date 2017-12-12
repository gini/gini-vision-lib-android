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

/**
 * Parser of QRCode content strings for payment data.
 * <p>
 * Currently supports the BezahlCode and EPC069-12 formats.
 */
class PaymentQRCodeParser implements QRCodeParser<PaymentData> {

    private final List<QRCodeParser<PaymentData>> mParsers;

    PaymentQRCodeParser() {
        mParsers = new ArrayList<>(3);
        mParsers.add(new BezahlCodeParser());
        mParsers.add(new EPC069_12Parser());
    }

    /**
     * Parses the content of a QRCode to retrieve the payment data.
     *
     * @param qrCodeContent content of a QRCode
     * @return a {@link PaymentData} containing the payment information from the QRCode
     * @throws IllegalArgumentException if the QRCode did not conform to any of the supported formats
     */
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
