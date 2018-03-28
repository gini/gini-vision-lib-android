package net.gini.android.vision.internal.camera.photo;

import android.support.annotation.NonNull;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageDocument.ImportMethod;
import net.gini.android.vision.document.ImageDocument.Source;

/**
 * @exclude
 */
public final class PhotoFactory {

    public static Photo newPhotoFromJpeg(final byte[] bytes,
            final int orientation,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final Source source) {
        return new MutablePhoto(bytes, orientation, deviceOrientation, deviceType, source,
                ImportMethod.NONE, ImageDocument.ImageFormat.JPEG, false);
    }

    public static Photo newPhotoFromDocument(final ImageDocument document) {
        if (document.getFormat() == ImageDocument.ImageFormat.JPEG) {
            return new MutablePhoto(document);
        }
        return new ImmutablePhoto(document);
    }

    private PhotoFactory() {
    }
}
