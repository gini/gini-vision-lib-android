package net.gini.android.vision.internal.qrcode;

import android.net.Uri;
import android.support.annotation.NonNull;

import net.gini.android.vision.PaymentData;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * QRCode parser for the BezahlCode format.
 *
 * See also the
 * <a href="http://www.bezahlcode.de/wp-content/uploads/BezahlCode_TechDok.pdf">BezahlCode Specification</a>
 */
public class BezahlCodeParser implements QRCodeParser<PaymentData> {

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
        final String amount = uri.getQueryParameter("amount");
        return new PaymentData(paymentRecipient, paymentReference, iban, bic, amount);
    }
}
