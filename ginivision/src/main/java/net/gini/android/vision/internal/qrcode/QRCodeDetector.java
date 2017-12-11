package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.util.Size;

import java.util.List;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

interface QRCodeDetector {

    void detect(@NonNull byte[] image, @NonNull Size imageSize, int rotation);

    void release();

    void setListener(@Nullable Listener listener);

    interface Listener {

        void onQRCodesDetected(@NonNull final List<QRCode> qrCodes);
    }
}
