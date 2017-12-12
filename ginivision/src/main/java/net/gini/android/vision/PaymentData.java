package net.gini.android.vision;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Contains payment information required for transactions that were extracted from the document.
 * <p>
 * Payment data from
 * <a href="http://www.bezahlcode.de/wp-content/uploads/BezahlCode_TechDok.pdf">BezahlCode</a>
 * and <a href="https://www.europeanpaymentscouncil.eu/document-library/guidance-documents/quick-response-code-guidelines-enable-data-capture-initiation">EPC069-12</a>
 * (<a href="https://www.stuzza.at/de/zahlungsverkehr/qr-code.html">Stuzza (AT)</a> and <a href="https://www.girocode.de/rechnungsempfaenger/">GiroCode (DE)</a>)
 * QRCodes are detected and read.
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
        mPaymentRecipient = nullToEmpty(paymentRecipient);
        mPaymentReference = nullToEmpty(paymentReference);
        mIBAN = nullToEmpty(iban);
        mBIC = nullToEmpty(bic);
        mAmount = nullToEmpty(amount);
    }

    private String nullToEmpty(@Nullable final String str) {
        return TextUtils.isEmpty(str) ? "" : str;
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

    @NonNull
    public String getAmount() {
        return mAmount;
    }

    @NonNull
    public String getBIC() {
        return mBIC;
    }

    @NonNull
    public String getIBAN() {
        return mIBAN;
    }

    @NonNull
    public String getPaymentRecipient() {
        return mPaymentRecipient;
    }

    @NonNull
    public String getPaymentReference() {
        return mPaymentReference;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PaymentData that = (PaymentData) o;

        if (!mAmount.equals(that.mAmount)) {
            return false;
        }
        if (!mBIC.equals(that.mBIC)) {
            return false;
        }
        if (!mIBAN.equals(that.mIBAN)) {
            return false;
        }
        if (!mPaymentRecipient.equals(that.mPaymentRecipient)) {
            return false;
        }
        return mPaymentReference.equals(that.mPaymentReference);
    }

    @Override
    public int hashCode() {
        int result = mAmount.hashCode();
        result = 31 * result + mBIC.hashCode();
        result = 31 * result + mIBAN.hashCode();
        result = 31 * result + mPaymentRecipient.hashCode();
        result = 31 * result + mPaymentReference.hashCode();
        return result;
    }
}
