package net.gini.android.vision.internal.qrcode;

import android.graphics.ImageFormat;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

class QRCodeDetectorHandler extends Handler {

    static final int DETECT_QRCODE = 1;
    private static final Logger LOG = LoggerFactory.getLogger(QRCodeDetectorHandler.class);
    private final BarcodeDetector mBarcodeDetector;
    private final UIExecutor mUIExecutor;
    private QRCodeDetector.Listener mListener;

    QRCodeDetectorHandler(final Looper looper,
            final BarcodeDetector barcodeDetector) {
        super(looper);
        mBarcodeDetector = barcodeDetector;
        mUIExecutor = new UIExecutor();
    }

    @Override
    public void handleMessage(final Message msg) {
        if (msg.what == DETECT_QRCODE) {
            if (mListener == null) {
                return;
            }
            final ImageData imageData = (ImageData) msg.obj;
            // This corresponds to the rotation constants in {@link Frame}.
            final int rotationForFrame = imageData.rotation / 90;
            final Frame frame = new Frame.Builder()
                    .setImageData(ByteBuffer.wrap(imageData.image), imageData.imageSize.width,
                            imageData.imageSize.height, ImageFormat.NV21)
                    .setRotation(rotationForFrame)
                    .build();
            final SparseArray<Barcode> barcodes = mBarcodeDetector.detect(frame);
            if (barcodes.size() > 0) {
                mUIExecutor.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LOG.info("Detected QRCodes:\n{}", barcodesToString(barcodes));
                        final List<QRCode> qrCodes = new ArrayList<>(barcodes.size());
                        for (int i = 0; i < barcodes.size(); i++) {
                            final int key = barcodes.keyAt(i);
                            final Barcode barcode = barcodes.get(key);
                            qrCodes.add(new QRCode(barcode.rawValue));
                        }
                        mListener.onQRCodesDetected(qrCodes);
                    }
                });
            }
        } else {
            super.handleMessage(msg);
        }
    }

    private String barcodesToString(final SparseArray<Barcode> barcodes) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < barcodes.size(); i++) {
            if (i != 0) {
                builder.append("\n");
            }
            final int key = barcodes.keyAt(i);
            final Barcode barcode = barcodes.get(key);
            builder.append(barcode.rawValue);
        }
        return builder.toString();
    }

    void setListener(@Nullable final QRCodeDetector.Listener listener) {
        mListener = listener;
    }

    static class ImageData {

        final byte[] image;
        final Size imageSize;
        final int rotation;

        ImageData(final byte[] image,
                final Size imageSize, final int rotation) {
            this.image = image;
            this.imageSize = imageSize;
            this.rotation = rotation;
        }
    }
}
