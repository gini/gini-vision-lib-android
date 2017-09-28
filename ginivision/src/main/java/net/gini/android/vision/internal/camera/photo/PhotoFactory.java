package net.gini.android.vision.internal.camera.photo;

import android.support.annotation.NonNull;

import net.gini.android.vision.document.ImageDocument;

/**
 * @exclude
 */
public final class PhotoFactory {

    public static Photo newPhotoFromJpeg(final byte[] bytes,
            final int orientation,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final String source) {
        return new MutablePhoto(bytes, orientation, deviceOrientation, deviceType, source,
                "", ImageDocument.ImageFormat.JPEG, false);
    }

    public static Photo newPhotoFromDocument(final ImageDocument document) {
        switch (document.getFormat()) {
            case JPEG:
                return new MutablePhoto(document);
            default:
                return new ImmutablePhoto(document);
        }
    }
}
