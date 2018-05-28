package net.gini.android.vision.internal.storage;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.AsyncCallback;

/**
 * Created by Alpar Szotyori on 23.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * @exclude
 */
public class SaveUriAsyncTask extends AsyncTask<Uri, Void, Uri> {

    private final Context mContext;
    private final AsyncCallback<Uri, Exception> mListener;
    private final ImageDiskStore mImageDiskStore;

    public static SaveUriAsyncTask execute(@NonNull final Uri uri,
            @NonNull final Context context,
            @NonNull final ImageDiskStore imageDiskStore,
            @NonNull final AsyncCallback<Uri, Exception> listener) {
        final SaveUriAsyncTask asyncTask = new SaveUriAsyncTask(context, imageDiskStore, listener);
        asyncTask.execute(uri);
        return asyncTask;
    }

    public SaveUriAsyncTask(@NonNull final Context context,
            @NonNull final ImageDiskStore imageDiskStore,
            @NonNull final AsyncCallback<Uri, Exception> listener) {
        mContext = context;
        mImageDiskStore = imageDiskStore;
        mListener = listener;
    }

    @Override
    protected Uri doInBackground(final Uri... uris) {
        final Uri uri = uris[0];
        if (uri == null) {
            return null;
        }
        return mImageDiskStore.save(mContext, uri);
    }

    @Override
    protected void onPostExecute(final Uri uri) {
        mListener.onSuccess(uri);
    }
}
