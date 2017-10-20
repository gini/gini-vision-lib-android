package net.gini.android.vision.internal.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.util.UriHelper;

import java.io.IOException;

/**
 * @exclude
 */
public class UriReaderAsyncTask extends AsyncTask<Uri, Void, byte[]> {

    private final Context mContext;
    private final AsyncCallback<byte[]> mListener;
    private Exception mException;

    public UriReaderAsyncTask(@NonNull final Context context,
            @NonNull final AsyncCallback<byte[]> listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected byte[] doInBackground(final Uri... uris) {
        try {
            return UriHelper.getBytesFromUri(uris[0], mContext);
        } catch (IOException e) {
            mException = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(final byte[] bytes) {
        if (mException != null) {
            mListener.onError(mException);
            return;
        }
        mListener.onSuccess(bytes);
    }
}
