package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;

import net.gini.android.vision.internal.util.Size;

import java.util.List;

/**
 * Created by Alpar Szotyori on 11.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public interface QRCodeDetectorTask {

    @NonNull
    List<QRCode> detect(@NonNull final byte[] image, @NonNull final Size imageSize,
            final int rotation);

    void release();

    boolean isOperational();

}
