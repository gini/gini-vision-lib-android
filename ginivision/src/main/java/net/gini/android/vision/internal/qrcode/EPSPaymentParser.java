package net.gini.android.vision.internal.qrcode;

import android.net.Uri;

import androidx.annotation.NonNull;
/**
 * Created by Alpar Szotyori on 15.04.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * QR Code parser for the eps e-payment QR Code url.
 * <p>
 * See the documentation of this format <a href="https://eservice.stuzza.at/de/eps-ueberweisung-dokumentation/category/5-dokumentation.html">here</a>.
 *
 * @suppress
 */
public class EPSPaymentParser implements QRCodeParser<PaymentQRCodeData> {

    public static final String EXTRACTION_ENTITY_NAME = "epsPaymentQRCodeUrl";
    private static final String EPSPAYMENT_SCHEME = "epspayment";

    @Override
    public PaymentQRCodeData parse(@NonNull final String qrCodeContent)
            throws IllegalArgumentException {
        final Uri uri = Uri.parse(qrCodeContent);
        if (!EPSPAYMENT_SCHEME.equals(uri.getScheme())) {
            throw new IllegalArgumentException(
                    "QRCode content does not conform to the eps e-payment QRCodeUrl format.");
        }
        return new PaymentQRCodeData(PaymentQRCodeData.Format.EPS_PAYMENT, qrCodeContent, null,
                null, null, null, null);
    }
}
