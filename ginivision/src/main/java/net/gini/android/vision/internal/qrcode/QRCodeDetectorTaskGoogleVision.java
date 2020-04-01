package net.gini.android.vision.internal.qrcode;

import android.content.Context;
import android.graphics.ImageFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * QRCode detector task using the Google Mobile Vision API.
 *
 * @suppress
 */
public class QRCodeDetectorTaskGoogleVision implements QRCodeDetectorTask {

    private static final boolean DEBUG = false;
    private static final Logger LOG = LoggerFactory.getLogger(QRCodeDetectorTaskGoogleVision.class);
    private final BarcodeDetector mBarcodeDetector;
    private final Handler mRetryHandler;
    private CheckAvailabilityRetryRunnable mCheckAvailabilityRetryRunnable;

    public QRCodeDetectorTaskGoogleVision(@NonNull final Context context) {
        mBarcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(
                Barcode.QR_CODE).build();
        mRetryHandler = new Handler(Looper.getMainLooper());
    }

    @NonNull
    @Override
    public List<String> detect(@NonNull final byte[] image, @NonNull final Size imageSize,
            final int rotation) {
        // This corresponds to the rotation constants in {@link Frame}.
        final int rotationForFrame = rotation / 90;
        final Frame frame = new Frame.Builder()
                .setImageData(ByteBuffer.wrap(image), imageSize.width,
                        imageSize.height, ImageFormat.NV21)
                .setRotation(rotationForFrame)
                .build();
        final SparseArray<Barcode> barcodes = mBarcodeDetector.detect(frame);
        if (barcodes.size() > 0 && DEBUG) {
            LOG.debug("Detected QRCodes:\n{}", barcodesToString(barcodes));
        }
        return barcodesToStrings(barcodes);
    }

    @Override
    public void checkAvailability(@NonNull final Callback callback) {
        if (mCheckAvailabilityRetryRunnable != null) {
            mCheckAvailabilityRetryRunnable.stop();
        }
        mCheckAvailabilityRetryRunnable = new CheckAvailabilityRetryRunnable(mBarcodeDetector,
                mRetryHandler, callback);
        mRetryHandler.post(mCheckAvailabilityRetryRunnable);
    }

    @Override
    public void release() {
        mBarcodeDetector.release();
        if (mCheckAvailabilityRetryRunnable != null) {
            mCheckAvailabilityRetryRunnable.stop();
            mRetryHandler.removeCallbacks(mCheckAvailabilityRetryRunnable);
        }
    }

    private List<String> barcodesToStrings(final SparseArray<Barcode> barcodes) {
        final List<String> qrCodes = new ArrayList<>(barcodes.size());
        for (int i = 0; i < barcodes.size(); i++) {
            final int key = barcodes.keyAt(i);
            final Barcode barcode = barcodes.get(key);
            qrCodes.add(barcode.rawValue);
        }
        return qrCodes;
    }

    private String barcodesToString(final SparseArray<Barcode> barcodes) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < barcodes.size(); i++) {
            if (i != 0) {
                builder.append('\n');
            }
            final int key = barcodes.keyAt(i);
            final Barcode barcode = barcodes.get(key);
            builder.append(barcode.rawValue);
        }
        return builder.toString();
    }

    static class CheckAvailabilityRetryRunnable implements Runnable {

        private static final int RETRY_DELAY_MS = 500;
        private static final int RETRY_LIMIT = 3;
        private final BarcodeDetector mBarcodeDetector;
        private final Callback mCallback;
        private final Handler mRetryHandler;
        private boolean mHasFinished;
        private int mRetries;

        CheckAvailabilityRetryRunnable(final BarcodeDetector barcodeDetector,
                final Handler retryHandler, final Callback callback) {
            mCallback = callback;
            mBarcodeDetector = barcodeDetector;
            mRetryHandler = retryHandler;
        }

        @Override
        public void run() {
            if (mRetries <= RETRY_LIMIT) {
                if (mBarcodeDetector.isOperational()) {
                    mCallback.onResult(true);
                    mHasFinished = true;
                } else {
                    mRetryHandler.postDelayed(this, RETRY_DELAY_MS);
                    mRetries++;
                }
            } else {
                mCallback.onResult(false);
                mHasFinished = true;
            }
        }

        void stop() {
            if (mHasFinished) {
                return;
            }
            mCallback.onInterrupted();
        }
    }
}
