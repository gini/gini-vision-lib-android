package net.gini.android.vision.internal.document;

import android.support.annotation.NonNull;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactoryDocumentAsyncTask;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */

class ImageDocumentRenderer implements DocumentRenderer {

    private final ImageDocument mImageDocument;
    private Photo mPhoto;

    ImageDocumentRenderer(@NonNull final ImageDocument document) {
        mImageDocument = document;
    }

    @Override
    public void toBitmap(@NonNull final Size targetSize,
            @NonNull final Callback callback) {
        if (mPhoto == null) {
            final PhotoFactoryDocumentAsyncTask asyncTask = new PhotoFactoryDocumentAsyncTask(
                    new PhotoFactoryDocumentAsyncTask.Listener() {
                        @Override
                        public void onPhotoCreated(@NonNull final Photo photo) {
                            mPhoto = photo;
                            callback.onBitmapReady(mPhoto.getBitmapPreview(),
                                    mPhoto.getRotationForDisplay());
                        }
                    });
            asyncTask.execute(mImageDocument);
        } else {
            callback.onBitmapReady(mPhoto.getBitmapPreview(), mPhoto.getRotationForDisplay());
        }
    }
}
