package net.gini.android.vision.internal.qrcode;

import static net.gini.android.vision.internal.qrcode.AmountAndCurrencyNormalizer.normalizeAmount;
import static net.gini.android.vision.internal.qrcode.AmountAndCurrencyNormalizer.normalizeCurrency;

import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * QRCode parser for the BezahlCode format.
 * <p>
 * See also the
 * <a href="http://www.bezahlcode.de/wp-content/uploads/BezahlCode_TechDok.pdf">BezahlCode
 * Specification</a>
 */
class BezahlCodeParser implements QRCodeParser<PaymentQRCodeData> {

    private final IBANValidator mIBANValidator;

    BezahlCodeParser() {
        mIBANValidator = new IBANValidator();
    }

    @Override
    public PaymentQRCodeData parse(@NonNull final String qrCodeContent)
            throws IllegalArgumentException {
        final Uri uri = Uri.parse(qrCodeContent);
        if (!"bank".equals(uri.getScheme())) {
            throw new IllegalArgumentException(
                    "QRCode content does not conform to the BezahlCode format.");
        }
        final String paymentRecipient = getQueryParameter(uri, "name");
        final String paymentReference = getQueryParameter(uri, "reason");
        final String iban = getQueryParameter(uri, "iban");
        try {
            mIBANValidator.validate(iban);
        } catch (final IBANValidator.IllegalIBANException e) {
            throw new IllegalArgumentException("Invalid IBAN in QRCode. " + e.getMessage(), e);
        }
        final String bic = getQueryParameter(uri, "bic");
        String currency = normalizeCurrency(getQueryParameter(uri, "currency"));
        currency = TextUtils.isEmpty(currency) ? "EUR" : currency;
        final String amount = normalizeAmount(getQueryParameter(uri, "amount"), currency);
        return new PaymentQRCodeData(PaymentQRCodeData.Format.BEZAHL_CODE, qrCodeContent,
                paymentRecipient, paymentReference, iban, bic, amount);
    }

    private String getQueryParameter(@NonNull final Uri uri, @NonNull final String key) {
        try {
            return uri.getQueryParameter(key);
        } catch (final UnsupportedOperationException e) {
            throw new IllegalArgumentException(
                    "QRCode content does not conform to the BezahlCode format: "
                            + e.getMessage(), e);
        }
    }
}
