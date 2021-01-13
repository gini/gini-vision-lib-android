package net.gini.android.vision.internal.qrcode;

import static net.gini.android.vision.internal.qrcode.AmountAndCurrencyNormalizer.normalizeAmount;

import android.text.TextUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * QRCode parser for the EPC069-12 format. See: <a href="https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation">EPC069-12
 * Specification</a>
 * <p>
 * This recommendation is implemented by Girocode (DE) and Stuzza (AT). Currently it supports
 * versions 1 and 2 and it does not honor the specified encoding.
 * <p>
 * See also the
 * <a href="https://www.stuzza.at/de/zahlungsverkehr/qr-code.html">"Zahlen mit Code"
 * Specification</a>
 */
class EPC069_12Parser implements QRCodeParser<PaymentQRCodeData> {

    private static final Logger LOG = LoggerFactory.getLogger(EPC069_12Parser.class);
    private final IBANValidator mIBANValidator;

    EPC069_12Parser() {
        mIBANValidator = new IBANValidator();
    }

    @Override
    public PaymentQRCodeData parse(@NonNull final String qrCodeContent)
            throws IllegalArgumentException {
        final String[] lines = qrCodeContent.split("\r\n|\n", 12);
        if (lines.length == 0 || !"BCD".equals(lines[0])) {
            throw new IllegalArgumentException(
                    "QRCode content does not conform to the EPC069-12 format.");
        }
        checkFormat(lines);
        final String paymentRecipient = getLineString(5, lines);
        final String paymentReference = concatPaymentReferenceLines(getLineString(9, lines),
                getLineString(10, lines));
        final String iban = getLineString(6, lines);
        try {
            mIBANValidator.validate(iban);
        } catch (final IBANValidator.IllegalIBANException e) {
            throw new IllegalArgumentException("Invalid IBAN in QRCode. " + e.getMessage(), e);
        }
        final String bic = getLineString(4, lines);
        final String amount = normalizeAmount(processAmount(getLineString(7, lines)), "EUR");
        return new PaymentQRCodeData(PaymentQRCodeData.Format.EPC069_12, qrCodeContent,
                paymentRecipient, paymentReference, iban, bic, amount);
    }

    @NonNull
    private String processAmount(@Nullable final String amount) {
        if (TextUtils.isEmpty(amount)) {
            return "";
        }
        return amount.length() > 3 ? amount.substring(3) : "";
    }

    private String getLineString(final int lineNr, final String[] lines) {
        return lines.length > lineNr ? lines[lineNr] : "";
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
            builder.append(referenceNr).append(' ');
        }
        if (!TextUtils.isEmpty(referenceText)) {
            builder.append(referenceText);
        }
        return builder.toString();
    }
}
