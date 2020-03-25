package net.gini.android.vision.internal.camera.photo;

import android.os.AsyncTask;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.document.ImageDocument;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public class PhotoFactoryDocumentAsyncTask extends AsyncTask<ImageDocument, Void, Photo> {

    private final AsyncCallback<Photo, Exception> mListener;
    private Exception mException;

    public PhotoFactoryDocumentAsyncTask(@NonNull final AsyncCallback<Photo, Exception> listener) {
        mListener = listener;
    }

    @Override
    protected Photo doInBackground(final ImageDocument... imageDocuments) {
        try {
            return PhotoFactory.newPhotoFromDocument(imageDocuments[0]);
        } catch (final Exception e) {
            mException = e;
            return null;
        }
    }

    @Override
    protected void onPostExecute(final Photo photo) {
        if (mException != null) {
            mListener.onError(mException);
            return;
        }
        mListener.onSuccess(photo);
    }

}
