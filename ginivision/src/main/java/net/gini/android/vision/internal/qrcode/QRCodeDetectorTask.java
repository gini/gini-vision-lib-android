package net.gini.android.vision.internal.qrcode;

import net.gini.android.vision.internal.util.Size;

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
 * Interface for synchronous detection of QRCodes from images.
 *
 * @suppress
 */
public interface QRCodeDetectorTask {

    @NonNull
    List<String> detect(@NonNull final byte[] image, @NonNull final Size imageSize,
            final int rotation);

    void checkAvailability(@NonNull final Callback callback);

    void release();

    /**
     * Internal use only.
     *
     * @suppress
     */
    interface Callback {

        void onResult(final boolean isAvailable);

        void onInterrupted();
    }

}
