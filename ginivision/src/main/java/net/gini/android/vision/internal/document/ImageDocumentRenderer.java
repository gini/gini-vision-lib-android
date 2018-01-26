package net.gini.android.vision.internal.document;

import android.support.annotation.NonNull;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.AsyncCallback;
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
                    new AsyncCallback<Photo>() {
                        @Override
                        public void onSuccess(final Photo result) {
                            mPhoto = result;
                            callback.onBitmapReady(mPhoto.getBitmapPreview(),
                                    mPhoto.getRotationForDisplay());
                        }

                        @Override
                        public void onError(final Exception exception) {
                            callback.onBitmapReady(null, 0);
                        }
                    });
            asyncTask.execute(mImageDocument);
        } else {
            callback.onBitmapReady(mPhoto.getBitmapPreview(), mPhoto.getRotationForDisplay());
        }
    }

    @Override
    public void getPageCount(@NonNull final AsyncCallback<Integer> asyncCallback) {
        asyncCallback.onSuccess(1);
    }
}
