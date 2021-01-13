package net.gini.android.vision.internal.qrcode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Parser of QRCode content strings for payment data.
 * <p>
 * Currently supports the
 * <a href="http://www.bezahlcode.de/wp-content/uploads/BezahlCode_TechDok.pdf">BezahlCode</a>
 * and <a href="https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation">EPC069-12</a>
 * (<a href="https://www.stuzza.at/de/zahlungsverkehr/qr-code.html">Stuzza (AT)</a> and <a href="https://www.girocode.de/rechnungsempfaenger/">GiroCode (DE)</a>)
 * QRCode formats.
 */
class PaymentQRCodeParser implements QRCodeParser<PaymentQRCodeData> {

    private final List<QRCodeParser<PaymentQRCodeData>> mParsers;

    PaymentQRCodeParser() {
        mParsers = new ArrayList<>(3);
        mParsers.add(new BezahlCodeParser());
        mParsers.add(new EPC069_12Parser());
        mParsers.add(new EPSPaymentParser());
    }

    /**
     * Parses the content of a QRCode to retrieve the payment data.
     *
     * @param qrCodeContent content of a QRCode
     * @return a {@link PaymentQRCodeData} containing the payment information from the QRCode
     * @throws IllegalArgumentException if the QRCode did not conform to any of the supported formats
     */
    @NonNull
    @Override
    public PaymentQRCodeData parse(@NonNull final String qrCodeContent)
            throws IllegalArgumentException {
        for (final QRCodeParser<PaymentQRCodeData> parser : mParsers) {
            try {
                return parser.parse(qrCodeContent);
            } catch (final IllegalArgumentException ignore) { // NOPMD
            }
        }
        throw new IllegalArgumentException("Unknown QRCode content format.");
    }
}
