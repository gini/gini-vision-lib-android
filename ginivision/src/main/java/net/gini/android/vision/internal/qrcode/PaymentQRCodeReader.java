package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import net.gini.android.vision.PaymentData;
import net.gini.android.vision.internal.util.Size;

import java.util.List;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Reads the first supported QRCode payment data from images.
 * <p>
 * See {@link PaymentQRCodeParser} for supported formats.
 */
public class PaymentQRCodeReader {

    private final QRCodeDetector mDetector;
    private final QRCodeParser<PaymentData> mParser;
    private Listener mListener = new Listener() {
        @Override
        public void onPaymentDataAvailable(@NonNull final PaymentData paymentData) {
        }
    };

    /**
     * Create a new instance which uses the provided {@link QRCodeDetectorTask} to do QRCode
     * detection.
     *
     * @param qrCodeDetectorTask a {@link QRCodeDetectorTask} implementation
     * @return new instance
     */
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

    @VisibleForTesting
    QRCodeDetector getDetector() {
        return mDetector;
    }

    /**
     * Reads the first supported QRCode payment data from the image.
     *
     * @param image an image byte array
     * @param imageSize size of the image
     * @param rotation rotation to be applied to the image for correct orientation
     */
    public void readFromImage(@NonNull final byte[] image, @NonNull final Size imageSize,
            final int rotation) {
        mDetector.detect(image, imageSize, rotation);
    }

    /**
     * Release all resources. Detection not possible after this has been called.
     */
    public void release() {
        mDetector.release();
    }

    public void setListener(@Nullable final Listener listener) {
        mListener = listener;
    }

    public interface Listener {

        /**
         * Called when a QRCode was found containing a supported payment data format.
         *
         * @param paymentData the payment data found on the image
         */
        void onPaymentDataAvailable(@NonNull final PaymentData paymentData);
    }
}
