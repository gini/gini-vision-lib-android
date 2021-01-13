package net.gini.android.vision.internal.qrcode;

import static net.gini.android.vision.internal.qrcode.QRCodeDetectorHandler.DETECT_QRCODE;

import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import net.gini.android.vision.internal.util.Size;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 08.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * QRCode detector executing detection on a background thread.
 */
class QRCodeDetectorImpl implements QRCodeDetector {

    private final QRCodeDetectorHandler mHandler;
    private final HandlerThread mHandlerThread;
    private Listener mListener;

    QRCodeDetectorImpl(@NonNull final QRCodeDetectorTask qrCodeDetectorTask) {
        mHandlerThread = new HandlerThread("QRCodeDetectorThread",
                Process.THREAD_PRIORITY_BACKGROUND);
        mHandlerThread.start();
        mHandler = new QRCodeDetectorHandler(mHandlerThread.getLooper(), qrCodeDetectorTask);
    }

    @Override
    public void detect(@NonNull final byte[] image, @NonNull final Size imageSize,
            final int rotation) {
        // If there is no listener, we don't process the image to avoid unnecessary computation
        if (mListener == null) {
            return;
        }
        mHandler.removeMessages(DETECT_QRCODE);
        final Message message = mHandler.obtainMessage(DETECT_QRCODE,
                new QRCodeDetectorHandler.ImageData(image, imageSize, rotation));
        mHandler.sendMessageAtFrontOfQueue(message);
    }

    @Override
    public void release() {
        mHandler.removeMessages(DETECT_QRCODE);
        mHandler.release();
        mHandlerThread.quit();
    }

    @Override
    public void setListener(@Nullable final Listener listener) {
        mListener = listener;
        if (mListener == null) {
            mHandler.setListener(null);
            return;
        }
        mHandler.setListener(new Listener() {
            @Override
            public void onQRCodesDetected(@NonNull final List<String> qrCodes) {
                if (mListener == null) {
                    return;
                }
                mListener.onQRCodesDetected(qrCodes);
            }
        });
    }

}
