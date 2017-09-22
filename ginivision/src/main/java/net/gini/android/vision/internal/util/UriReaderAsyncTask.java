package net.gini.android.vision.internal.util;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;

/**
 * @exclude
 */
public class UriReaderAsyncTask extends AsyncTask<Uri, Void, byte[]> {

    private final Context mContext;
    private final Listener mListener;
    private Exception mException;

    public UriReaderAsyncTask(@NonNull final Context context,
            @NonNull final Listener listener) {
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
        mListener.onBytesRead(bytes);
    }

    public interface Listener {
        void onBytesRead(@Nullable final byte[] bytes);
        void onError(@NonNull final Exception exception);
    }
}
