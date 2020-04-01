package net.gini.android.vision.internal.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.util.UriHelper;

import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
public class UriReaderAsyncTask extends AsyncTask<Uri, Void, byte[]> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final AsyncCallback<byte[], Exception> mListener;
    private Exception mException;

    public UriReaderAsyncTask(@NonNull final Context context,
            @NonNull final AsyncCallback<byte[], Exception> listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected byte[] doInBackground(final Uri... uris) {
        try {
            return UriHelper.getBytesFromUri(uris[0], mContext);
        } catch (final IOException e) {
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
