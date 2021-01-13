package net.gini.android.vision.internal.qrcode;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import net.gini.android.vision.internal.camera.api.UIExecutor;
import net.gini.android.vision.internal.util.Size;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Handler to execute QRCode detection. To be used with a {@link Looper} and enables detection on
 * background threads.
 */
class QRCodeDetectorHandler extends Handler {

    static final int DETECT_QRCODE = 1;
    private final QRCodeDetectorTask mQRCodeDetectorTask;
    private final UIExecutor mUIExecutor;
    private QRCodeDetector.Listener mListener;

    QRCodeDetectorHandler(final Looper looper,
            final QRCodeDetectorTask qrCodeDetectorTask) {
        super(looper);
        mQRCodeDetectorTask = qrCodeDetectorTask;
        mUIExecutor = new UIExecutor();
    }

    @Override
    public void handleMessage(final Message msg) {
        if (msg.what == DETECT_QRCODE) {
            if (mListener == null) {
                return;
            }
            final ImageData imageData = (ImageData) msg.obj;
            final List<String> qrCodes = mQRCodeDetectorTask.detect(imageData.image,
                    imageData.imageSize, imageData.rotation);
            if (!qrCodes.isEmpty()) {
                mUIExecutor.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListener.onQRCodesDetected(qrCodes);
                    }
                });
            }
        } else {
            super.handleMessage(msg);
        }
    }

    void release() {
        mQRCodeDetectorTask.release();
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
