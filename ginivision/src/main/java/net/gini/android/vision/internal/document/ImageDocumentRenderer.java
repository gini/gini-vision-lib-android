package net.gini.android.vision.internal.document;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */

public class ImageDocumentRenderer implements DocumentRenderer {

    private final Photo mPhoto;

    public ImageDocumentRenderer(@NonNull final ImageDocument document) {
        mPhoto = Photo.fromDocument(document);
    }

    @Nullable
    @Override
    public Bitmap toBitmap(@NonNull final Size targetSize) {
        return mPhoto.getBitmapPreview();
    }

    @Override
    public int getRotationForDisplay() {
        return mPhoto.getRotationForDisplay();
    }
}
