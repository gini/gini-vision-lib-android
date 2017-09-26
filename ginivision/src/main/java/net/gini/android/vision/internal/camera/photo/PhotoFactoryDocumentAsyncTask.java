package net.gini.android.vision.internal.camera.photo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.AsyncCallback;

/**
 * @exclude
 */
public class PhotoFactoryDocumentAsyncTask extends AsyncTask<ImageDocument, Void, Photo> {

    private final AsyncCallback<Photo> mListener;
    private Exception mException;

    public PhotoFactoryDocumentAsyncTask(@NonNull final AsyncCallback<Photo> listener) {
        mListener = listener;
    }

    @Override
    protected Photo doInBackground(final ImageDocument... imageDocuments) {
        try {
            return PhotoFactory.newPhotoFromDocument(imageDocuments[0]);
        } catch (Exception e) {
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
