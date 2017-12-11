package net.gini.android.vision.internal.qrcode;

import android.content.Context;
import android.graphics.ImageFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import net.gini.android.vision.internal.util.Size;

import java.nio.ByteBuffer;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

class QRCodeDetector {

    private final BarcodeDetector mBarcodeDetector;
    private Listener mListener;

    QRCodeDetector(@NonNull final Context context) {
        mBarcodeDetector = new BarcodeDetector.Builder(context).build();
    }

    public void release() {
        mBarcodeDetector.release();
    }

    public void setListener(@Nullable final Listener listener) {
        mListener = listener;
    }

    void detect(@NonNull final byte[] image, @NonNull final Size imageSize, final int rotation) {
        // This corresponds to the rotation constants in {@link Frame}.
        final int rotationForFrame = rotation / 90;
        final Frame frame = new Frame.Builder()
                .setImageData(ByteBuffer.wrap(image), imageSize.width,
                        imageSize.height, ImageFormat.NV21)
                .setRotation(rotationForFrame)
                .build();
        final SparseArray<Barcode> barcodes = mBarcodeDetector.detect(frame);
        for (int i = 0; i < barcodes.size(); i++) {
            final int key = barcodes.keyAt(i);
            final Barcode barcode = barcodes.get(key);
            mListener.onQRCodeDetected(new QRCode(barcode.rawValue));
        }
    }

    public interface Listener {

        void onQRCodeDetected(@NonNull final QRCode qrCode);
    }
}
