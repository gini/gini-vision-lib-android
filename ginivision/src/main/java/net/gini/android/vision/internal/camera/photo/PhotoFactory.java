package net.gini.android.vision.internal.camera.photo;

import net.gini.android.vision.document.ImageDocument;

/**
 * @exclude
 */
public final class PhotoFactory {

    public static Photo newPhotoFromJpeg(final byte[] bytes, final int orientation,
            final String deviceOrientation, final String deviceType) {
        return new MutablePhoto(bytes, orientation, deviceOrientation, deviceType,
                ImageDocument.ImageFormat.JPEG, false);
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
