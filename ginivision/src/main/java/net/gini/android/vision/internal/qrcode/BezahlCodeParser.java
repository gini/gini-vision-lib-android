package net.gini.android.vision.internal.qrcode;

import static net.gini.android.vision.internal.qrcode.AmountAndCurrencyNormalizer.normalizeAmount;
import static net.gini.android.vision.internal.qrcode.AmountAndCurrencyNormalizer.normalizeCurrency;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import net.gini.android.vision.PaymentData;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * QRCode parser for the BezahlCode format.
 * <p>
 * See also the
 * <a href="http://www.bezahlcode.de/wp-content/uploads/BezahlCode_TechDok.pdf">BezahlCode Specification</a>
 */
class BezahlCodeParser implements QRCodeParser<PaymentData> {

    private final IBANValidator mIBANValidator;

    BezahlCodeParser() {
        mIBANValidator = new IBANValidator();
    }

    @Override
    public PaymentData parse(@NonNull final String qrCodeContent)
            throws IllegalArgumentException {
        final Uri uri = Uri.parse(qrCodeContent);
        if (!"bank".equals(uri.getScheme())) {
            throw new IllegalArgumentException(
                    "QRCode content does not conform to the BezahlCode format.");
        }
        final String paymentRecipient = uri.getQueryParameter("name");
        final String paymentReference = uri.getQueryParameter("reason");
        final String iban = uri.getQueryParameter("iban");
        try {
            mIBANValidator.validate(iban);
        } catch (final IBANValidator.IllegalIBANException e) {
            throw new IllegalArgumentException("Invalid IBAN in QRCode. " + e.getMessage(), e);
        }
        final String bic = uri.getQueryParameter("bic");
        String currency = normalizeCurrency(uri.getQueryParameter("currency"));
        currency = TextUtils.isEmpty(currency) ? "EUR" : currency;
        final String amount = normalizeAmount(uri.getQueryParameter("amount"), currency);
        return new PaymentData(paymentRecipient, paymentReference, iban, bic, amount);
    }
}
