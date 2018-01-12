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
