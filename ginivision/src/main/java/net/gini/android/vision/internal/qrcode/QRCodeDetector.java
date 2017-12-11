package net.gini.android.vision.internal.qrcode;

import static net.gini.android.vision.internal.qrcode.QRCodeDetectorHandler.DETECT_QRCODE;

import android.content.Context;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

class QRCodeDetector {

    private static final Logger LOG = LoggerFactory.getLogger(QRCodeDetector.class);
    private final BarcodeDetector mBarcodeDetector;
    private final QRCodeDetectorHandler mHandler;
    private final HandlerThread mHandlerThread;
    private Listener mListener;

    QRCodeDetector(@NonNull final Context context) {
        mBarcodeDetector = new BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.QR_CODE)
                .build();
        mHandlerThread = new HandlerThread("QRCodeDetectorThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mHandler = new QRCodeDetectorHandler(mHandlerThread.getLooper(), mBarcodeDetector);
    }

    void detect(@NonNull final byte[] image, @NonNull final Size imageSize, final int rotation) {
        if (mListener == null) {
            return;
        }
        mHandler.removeMessages(DETECT_QRCODE);
        final Message message = mHandler.obtainMessage(DETECT_QRCODE,
                new QRCodeDetectorHandler.ImageData(image, imageSize, rotation));
        mHandler.sendMessageAtFrontOfQueue(message);
    }

    void release() {
        mHandler.removeMessages(DETECT_QRCODE);
        mHandlerThread.quit();
        mBarcodeDetector.release();
        LOG.debug("Released");
    }

    void setListener(@Nullable final Listener listener) {
        mListener = listener;
        mHandler.setListener(listener);
    }

    interface Listener {

        void onQRCodesDetected(@NonNull final List<QRCode> qrCodes);
    }


}
