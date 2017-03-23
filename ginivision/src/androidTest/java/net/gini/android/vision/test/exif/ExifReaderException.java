package net.gini.android.vision.test.exif;

import android.support.annotation.NonNull;

public class ExifReaderException extends RuntimeException {

    ExifReaderException(@NonNull final String detailMessage) {
        super(detailMessage);
    }

    ExifReaderException(@NonNull final String detailMessage, @NonNull final Throwable throwable) {
        super(detailMessage, throwable);
    }
}
