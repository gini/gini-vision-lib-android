package net.gini.android.vision;

import static com.google.common.truth.Truth.assertThat;

import net.gini.android.vision.internal.qrcode.PaymentQRCodeData;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Created by Alpar Szotyori on 12.01.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

@RunWith(AndroidJUnit4.class)
public class PaymentQRCodeDataTest {

    @Test
    public void should_writePaymentData_toJson() {
        // Given
        final PaymentQRCodeData paymentData = new PaymentQRCodeData(
                PaymentQRCodeData.Format.BEZAHL_CODE,
                "bank://singlepaymentsepa?name=GINI%20GMBH&reason=BezahlCode%20Test&iban=DE27100777770209299700&bic=DEUTDEMMXXX&amount=140%2C4",
                "GINI GMBH",
                "BezahlCode Test",
                "DE27100777770209299700",
                "DEUTDEMMXXX",
                "140.40:EUR");
        // When
        final String json = paymentData.toJson();
        // Then
        assertThat(json).isEqualTo(
                "{\"qrcode\":\"bank://singlepaymentsepa?name=GINI%20GMBH&reason=BezahlCode%20Test&iban=DE27100777770209299700&bic=DEUTDEMMXXX&amount=140%2C4\",\"paymentdata\":{\"amountToPay\":\"140.40:EUR\",\"paymentRecipient\":\"GINI GMBH\",\"iban\":\"DE27100777770209299700\",\"bic\":\"DEUTDEMMXXX\",\"paymentReference\":\"BezahlCode Test\"}}");
    }

    @Test
    public void should_notWrite_emptyLabel_toPaymentDataJson() {
        // Given
        final PaymentQRCodeData paymentData = new PaymentQRCodeData(
                PaymentQRCodeData.Format.BEZAHL_CODE,
                "bank://singlepaymentsepa?name=GINI%20GMBH&reason=BezahlCode%20Test&iban=DE27100777770209299700&bic=DEUTDEMMXXX&amount=140%2C4",
                "GINI GMBH",
                "BezahlCode Test",
                "DE27100777770209299700",
                null,
                "");
        // When
        final String json = paymentData.toJson();
        // Then
        assertThat(json).isEqualTo(
                "{\"qrcode\":\"bank://singlepaymentsepa?name=GINI%20GMBH&reason=BezahlCode%20Test&iban=DE27100777770209299700&bic=DEUTDEMMXXX&amount=140%2C4\",\"paymentdata\":{\"paymentRecipient\":\"GINI GMBH\",\"iban\":\"DE27100777770209299700\",\"paymentReference\":\"BezahlCode Test\"}}");
    }
}