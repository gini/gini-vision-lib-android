package net.gini.android.vision.internal.camera.photo;

import net.gini.android.vision.document.ImageDocument;

/**
 * @exclude
 */
public final class PhotoFactory {

    public static Photo fromJpeg(final byte[] bytes, final int orientation,
            final String deviceOrientation, final String deviceType) {
        return new MutablePhoto(bytes, orientation, deviceOrientation, deviceType,
                ImageDocument.ImageFormat.JPEG);
    }

    public static Photo fromDocument(final ImageDocument document) {
        switch (document.getFormat()) {
            case JPEG:
            case TIFF:
                return new MutablePhoto(document);
            default:
                return new ImmutablePhoto(document);
        }
    }
}
