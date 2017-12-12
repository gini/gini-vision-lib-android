package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.PaymentData;
import net.gini.android.vision.internal.util.Size;

import java.util.List;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class PaymentQRCodeReader {

    private final QRCodeDetector mDetector;
    private final QRCodeParser<PaymentData> mParser;
    private Listener mListener = new Listener() {
        @Override
        public void onPaymentDataAvailable(@NonNull final PaymentData paymentData) {
        }
    };

    public static PaymentQRCodeReader newInstance(
            @NonNull final QRCodeDetectorTask qrCodeDetectorTask) {
        return new PaymentQRCodeReader(
                new QRCodeDetectorImpl(qrCodeDetectorTask),
                new PaymentQRCodeParser());
    }

    private PaymentQRCodeReader(
            @NonNull final QRCodeDetector detector,
            @NonNull final QRCodeParser<PaymentData> parser) {
        mDetector = detector;
        mParser = parser;
        mDetector.setListener(new QRCodeDetector.Listener() {
            @Override
            public void onQRCodesDetected(@NonNull final List<String> qrCodes) {
                for (final String qrCodeContent : qrCodes) {
                    try {
                        final PaymentData paymentData = mParser.parse(qrCodeContent);
                        mListener.onPaymentDataAvailable(paymentData);
                        return;
                    } catch (final IllegalArgumentException ignored) {
                    }
                }
            }
        });
    }

    public void readFromImage(@NonNull final byte[] image, @NonNull final Size imageSize,
            final int rotation) {
        mDetector.detect(image, imageSize, rotation);
    }

    public void release() {
        mDetector.release();
    }

    public void setListener(@Nullable final Listener listener) {
        mListener = listener;
    }

    public interface Listener {

        void onPaymentDataAvailable(@NonNull final PaymentData paymentData);
    }
}
