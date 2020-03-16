package net.gini.android.vision.internal.camera.photo;

import androidx.annotation.NonNull;

/**
 * @exclude
 */
class ExifReaderException extends RuntimeException {

    ExifReaderException(@NonNull final String detailMessage) {
        super(detailMessage);
    }

    ExifReaderException(@NonNull final String detailMessage, @NonNull final Throwable throwable) {
        super(detailMessage, throwable);
    }
}
