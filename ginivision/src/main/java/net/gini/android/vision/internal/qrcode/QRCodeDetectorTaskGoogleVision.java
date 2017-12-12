package net.gini.android.vision.internal.qrcode;

import android.content.Context;
import android.graphics.ImageFormat;
import android.support.annotation.NonNull;
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

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * QRCode detector task using the Google Mobile Vision API.
 */
public class QRCodeDetectorTaskGoogleVision implements QRCodeDetectorTask {

    private static final Logger LOG = LoggerFactory.getLogger(QRCodeDetectorTaskGoogleVision.class);
    private final BarcodeDetector mBarcodeDetector;

    public QRCodeDetectorTaskGoogleVision(@NonNull final Context context) {
        mBarcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(
                Barcode.QR_CODE).build();
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
        if (barcodes.size() > 0) {
            LOG.debug("Detected QRCodes:\n{}", barcodesToString(barcodes));
        }
        return barcodesToStrings(barcodes);
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
                builder.append("\n");
            }
            final int key = barcodes.keyAt(i);
            final Barcode barcode = barcodes.get(key);
            builder.append(barcode.rawValue);
        }
        return builder.toString();
    }

    @Override
    public boolean isOperational() {
        return mBarcodeDetector.isOperational();
    }

    @Override
    public void release() {
        mBarcodeDetector.release();
    }
}
