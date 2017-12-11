package net.gini.android.vision.internal.qrcode;

import android.support.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class PaymentData {

    private final String mAmount;
    private final String mBIC;
    private final String mIBAN;
    private final String mPaymentRecipient;
    private final String mPaymentReference;

    public PaymentData(@Nullable final String paymentRecipient,
            @Nullable final String paymentReference,
            @Nullable final String iban,
            @Nullable final String bic,
            @Nullable final String amount) {
        mPaymentRecipient = paymentRecipient;
        mPaymentReference = paymentReference;
        mIBAN = iban;
        mBIC = bic;
        mAmount = amount;
    }

    @Override
    public String toString() {
        return "PaymentData{" +
                "mAmount='" + mAmount + '\'' +
                ", mBIC='" + mBIC + '\'' +
                ", mIBAN='" + mIBAN + '\'' +
                ", mPaymentRecipient='" + mPaymentRecipient + '\'' +
                ", mPaymentReference='" + mPaymentReference + '\'' +
                '}';
    }

    @Nullable
    public String getAmount() {
        return mAmount;
    }

    @Nullable
    public String getBIC() {
        return mBIC;
    }

    @Nullable
    public String getIBAN() {
        return mIBAN;
    }

    @Nullable
    public String getPaymentRecipient() {
        return mPaymentRecipient;
    }

    @Nullable
    public String getPaymentReference() {
        return mPaymentReference;
    }
}
