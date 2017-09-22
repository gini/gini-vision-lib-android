package net.gini.android.vision.internal.camera.photo;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.document.ImageDocument;

/**
 * @exclude
 */
public class PhotoFactoryDocumentAsyncTask extends AsyncTask<ImageDocument, Void, Photo> {

    private final Listener mListener;

    public PhotoFactoryDocumentAsyncTask(@NonNull final Listener listener) {
        mListener = listener;
    }

    @Override
    protected Photo doInBackground(final ImageDocument... imageDocuments) {
        return PhotoFactory.newPhotoFromDocument(imageDocuments[0]);
    }

    @Override
    protected void onPostExecute(final Photo photo) {
        mListener.onPhotoCreated(photo);
    }

    public interface Listener {
        void onPhotoCreated(@NonNull final Photo photo);
    }
}
