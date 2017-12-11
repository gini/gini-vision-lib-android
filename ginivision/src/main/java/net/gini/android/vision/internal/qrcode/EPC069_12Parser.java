package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * QRCode parser for the EPC069-12 format.
 * See: https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation
 *
 * This recommendation is implemented by Girocode (DE) and Stuzza (AT).
 * Currently it supports versions 1 and 2 and it does not honor the specified encoding.
 *
 * See also the
 * <a href="https://www.stuzza.at/de/zahlungsverkehr/qr-code.html">"Zahlen mit Code" Specification</a>
 */
public class EPC069_12Parser implements QRCodeParser<PaymentData> {

    private static final Logger LOG = LoggerFactory.getLogger(EPC069_12Parser.class);

    @Override
    public PaymentData parse(@NonNull final String qrCodeContent) throws IllegalArgumentException {
        final String[] lines = qrCodeContent.split("\r\n|\n", 12);
        if (lines.length < 12 || !"BCD".equals(lines[0])) {
            throw new IllegalArgumentException(
                    "QRCode content does not conform to the EPC069-12 format.");
        }
        checkFormat(lines);
        final String paymentRecipient = lines[5];
        final String paymentReference = concatPaymentReferenceLines(lines[9], lines[10]);
        final String iban = lines[6];
        final String bic = lines[4];
        final String amount = lines[7];
        return new PaymentData(paymentRecipient, paymentReference, iban, bic, amount);
    }

    private void checkFormat(@NonNull final String[] lines) {
        final int version = Integer.parseInt(lines[1]);
        final int encoding = Integer.parseInt(lines[2]);
        final String identificationCode = lines[3];

        if (version < 1 || version > 2) {
            LOG.warn("Unsupported version of EPC069-12 QRCode. Proceeding with fingers crossed!");
        }
        if (encoding != 1) {
            LOG.warn("Unsupported encoding in EPC069-12 QRCode. Proceeding with fingers crossed!");
        }
        if (!"SCT".equals(identificationCode)) {
            LOG.warn(
                    "Unsupported identificationCode in EPC069-12 QRCode. Proceeding with fingers crossed!");
        }

    }

    private String concatPaymentReferenceLines(final String referenceNr,
            final String referenceText) {
        final StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(referenceNr)) {
            builder.append(referenceNr).append(" ");
        }
        if (!TextUtils.isEmpty(referenceText)) {
            builder.append(referenceText);
        }
        return builder.toString();
    }
}
