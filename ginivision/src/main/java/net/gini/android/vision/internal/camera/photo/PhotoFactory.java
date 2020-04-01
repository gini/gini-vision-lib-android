package net.gini.android.vision.internal.camera.photo;

import net.gini.android.vision.Document;
import net.gini.android.vision.document.ImageDocument;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public final class PhotoFactory {

    public static Photo newPhotoFromJpeg(final byte[] bytes,
            final int orientation,
            @NonNull final String deviceOrientation,
            @NonNull final String deviceType,
            @NonNull final Document.Source source) {
        return new MutablePhoto(bytes, orientation, deviceOrientation, deviceType, source,
                Document.ImportMethod.NONE, ImageDocument.ImageFormat.JPEG, false);
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
