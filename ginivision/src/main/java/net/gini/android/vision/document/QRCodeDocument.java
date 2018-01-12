package net.gini.android.vision.document;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.internal.qrcode.PaymentQRCodeData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

/**
 * Created by Alpar Szotyori on 12.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Contains payment information required for transactions that were parsed from a document with a QR Code.
 * <p>
 * Payment data from
 * <a href="http://www.bezahlcode.de/wp-content/uploads/BezahlCode_TechDok.pdf">BezahlCode</a>
 * and <a href="https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation">EPC069-12</a>
 * (<a href="https://www.stuzza.at/de/zahlungsverkehr/qr-code.html">Stuzza (AT)</a> and <a href="https://www.girocode.de/rechnungsempfaenger/">GiroCode (DE)</a>)
 * QRCodes are detected and read.
 * <p>
 * Get the contents with ({@link QRCodeDocument#getData()}) and upload it to the Gini API to get the extractions.
 */
public class QRCodeDocument extends GiniVisionDocument {

    private static final Logger LOG = LoggerFactory.getLogger(QRCodeDocument.class);

    public static QRCodeDocument fromPaymentQRCodeData(@NonNull final PaymentQRCodeData paymentQRCodeData) {
        byte[] jsonBytes = new byte[]{};
        try {
            jsonBytes = paymentQRCodeData.toJson().getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOG.error("UTF-8 encoding not available", e);
        }
        return new QRCodeDocument(jsonBytes, paymentQRCodeData);
    }

    private final PaymentQRCodeData mPaymentData;

    private QRCodeDocument(@NonNull final byte[] data, @NonNull final PaymentQRCodeData paymentQRCodeData) {
        super(Type.QRCode, data, null, false, false);
        mPaymentData = paymentQRCodeData;
    }

    private QRCodeDocument(final Parcel in) {
        super(in);
        mPaymentData = in.readParcelable(PaymentQRCodeData.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(mPaymentData, flags);
    }

    public static final Creator<QRCodeDocument> CREATOR = new Parcelable.Creator<QRCodeDocument>() {
        @Override
        public QRCodeDocument createFromParcel(final Parcel in) {
            return new QRCodeDocument(in);
        }

        @Override
        public QRCodeDocument[] newArray(final int size) {
            return new QRCodeDocument[size];
        }
    };

    /**
     * @exclude
     */
    @VisibleForTesting
    PaymentQRCodeData getPaymentData() {
        return mPaymentData;
    }
}
