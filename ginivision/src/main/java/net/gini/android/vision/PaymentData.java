package net.gini.android.vision;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.JsonWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;

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
public class PaymentData implements Parcelable {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentData.class);

    private final String mUnparsedContent;
    private final String mAmount;
    private final String mBIC;
    private final String mIBAN;
    private final String mPaymentRecipient;
    private final String mPaymentReference;

    public PaymentData(@NonNull final String unparsedContent,
            @Nullable final String paymentRecipient,
            @Nullable final String paymentReference,
            @Nullable final String iban,
            @Nullable final String bic,
            @Nullable final String amount) {
        mUnparsedContent = unparsedContent;
        mPaymentRecipient = nullToEmpty(paymentRecipient);
        mPaymentReference = nullToEmpty(paymentReference);
        mIBAN = nullToEmpty(iban);
        mBIC = nullToEmpty(bic);
        mAmount = nullToEmpty(amount);
    }

    private String nullToEmpty(@Nullable final String str) {
        return TextUtils.isEmpty(str) ? "" : str;
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
    public String toString() {
        return "PaymentData{" +
                "mUnparsedContent='" + mUnparsedContent + '\'' +
                ", mAmount='" + mAmount + '\'' +
                ", mBIC='" + mBIC + '\'' +
                ", mIBAN='" + mIBAN + '\'' +
                ", mPaymentRecipient='" + mPaymentRecipient + '\'' +
                ", mPaymentReference='" + mPaymentReference + '\'' +
                '}';
    }

    @NonNull
    public String toJson() {
        final StringWriter stringWriter = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(stringWriter);
        try {
            jsonWriter.beginObject();
            jsonWriter.name("qrcode").value(mUnparsedContent);
            jsonWriter.name("paymentdata");
            jsonWriter.beginObject();
            writeNameAndValueIfNotEmpty(jsonWriter, "amountToPay", mAmount);
            writeNameAndValueIfNotEmpty(jsonWriter, "paymentRecipient", mPaymentRecipient);
            writeNameAndValueIfNotEmpty(jsonWriter, "iban", mIBAN);
            writeNameAndValueIfNotEmpty(jsonWriter, "bic", mBIC);
            writeNameAndValueIfNotEmpty(jsonWriter, "paymentReference", mPaymentReference);
            jsonWriter.endObject();
            jsonWriter.endObject();
        } catch (final IOException e) {
            LOG.error("Could not write to json", e);
        } finally {
            try {
                jsonWriter.close();
            } catch (final IOException ignore) {
            }
        }
        return stringWriter.toString();
    }

    private void writeNameAndValueIfNotEmpty(@NonNull final JsonWriter jsonWriter,
            @Nullable final String name, @Nullable final String value) throws IOException {
        if (!TextUtils.isEmpty(value)) {
            jsonWriter.name(name).value(value);
        }
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

        if (!mUnparsedContent.equals(that.mUnparsedContent)) {
            return false;
        }
        if (mAmount != null ? !mAmount.equals(that.mAmount) : that.mAmount != null) {
            return false;
        }
        if (mBIC != null ? !mBIC.equals(that.mBIC) : that.mBIC != null) {
            return false;
        }
        if (mIBAN != null ? !mIBAN.equals(that.mIBAN) : that.mIBAN != null) {
            return false;
        }
        if (mPaymentRecipient != null ? !mPaymentRecipient.equals(that.mPaymentRecipient)
                : that.mPaymentRecipient != null) {
            return false;
        }
        return mPaymentReference != null ? mPaymentReference.equals(that.mPaymentReference)
                : that.mPaymentReference == null;
    }

    @Override
    public int hashCode() {
        int result = mUnparsedContent.hashCode();
        result = 31 * result + (mAmount != null ? mAmount.hashCode() : 0);
        result = 31 * result + (mBIC != null ? mBIC.hashCode() : 0);
        result = 31 * result + (mIBAN != null ? mIBAN.hashCode() : 0);
        result = 31 * result + (mPaymentRecipient != null ? mPaymentRecipient.hashCode() : 0);
        result = 31 * result + (mPaymentReference != null ? mPaymentReference.hashCode() : 0);
        return result;
    }

    private PaymentData(final Parcel in) {
        mUnparsedContent = in.readString();
        mAmount = in.readString();
        mBIC = in.readString();
        mIBAN = in.readString();
        mPaymentRecipient = in.readString();
        mPaymentReference = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
        dest.writeString(mUnparsedContent);
        dest.writeString(mAmount);
        dest.writeString(mBIC);
        dest.writeString(mIBAN);
        dest.writeString(mPaymentRecipient);
        dest.writeString(mPaymentReference);
    }

    public static final Creator<PaymentData> CREATOR = new Creator<PaymentData>() {
        @Override
        public PaymentData createFromParcel(final Parcel in) {
            return new PaymentData(in);
        }

        @Override
        public PaymentData[] newArray(final int size) {
            return new PaymentData[size];
        }
    };
}
