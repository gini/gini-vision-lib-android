package net.gini.android.vision.internal.qrcode;

import net.gini.android.vision.internal.util.Size;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Interface for asynchronous detection of QRCodes from images.
 */
interface QRCodeDetector {

    void detect(@NonNull byte[] image, @NonNull Size imageSize, int rotation);

    void release();

    void setListener(@Nullable Listener listener);

    interface Listener {

        /**
         * Called when QRCodes were detected.
         *
         * @param qrCodes list of QRCode content strings
         */
        void onQRCodesDetected(@NonNull final List<String> qrCodes);
    }
}
