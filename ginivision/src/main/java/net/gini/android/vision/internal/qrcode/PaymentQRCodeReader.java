package net.gini.android.vision.internal.qrcode;

import net.gini.android.vision.internal.util.Size;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * Reads the first supported QRCode payment data from images.
 * <p>
 * See {@link PaymentQRCodeParser} for supported formats.
 *
 * @suppress
 */
public class PaymentQRCodeReader {

    private final QRCodeDetector mDetector;
    private final QRCodeParser<PaymentQRCodeData> mParser;
    private Listener mListener = new Listener() {
        @Override
        public void onPaymentQRCodeDataAvailable(
                @NonNull final PaymentQRCodeData paymentQRCodeData) {
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
            @NonNull final QRCodeParser<PaymentQRCodeData> parser) {
        mDetector = detector;
        mParser = parser;
        mDetector.setListener(new QRCodeDetector.Listener() {
            @Override
            public void onQRCodesDetected(@NonNull final List<String> qrCodes) {
                for (final String qrCodeContent : qrCodes) {
                    try {
                        final PaymentQRCodeData paymentData = mParser.parse(qrCodeContent);
                        mListener.onPaymentQRCodeDataAvailable(paymentData);
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

    /**
     * Internal use only.
     *
     * @suppress
     */
    public interface Listener {

        /**
         * Called when a QRCode was found containing a supported payment data format.
         *
         * @param paymentQRCodeData the payment data found on the image
         */
        void onPaymentQRCodeDataAvailable(@NonNull final PaymentQRCodeData paymentQRCodeData);
    }
}
