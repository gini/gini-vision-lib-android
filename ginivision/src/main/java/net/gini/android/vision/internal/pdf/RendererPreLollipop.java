package net.gini.android.vision.internal.pdf;

import android.graphics.Bitmap;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.internal.util.Size;

import androidx.annotation.NonNull;

/**
 * Internal use only.
 *
 * @suppress
 */
class RendererPreLollipop implements Renderer {

    @Override
    public void toBitmap(@NonNull final Size targetSize,
            @NonNull final AsyncCallback<Bitmap, Exception> asyncCallback) {
        asyncCallback.onSuccess(null);
    }

    @Override
    public void getPageCount(@NonNull final AsyncCallback<Integer, Exception> asyncCallback) {
        asyncCallback.onSuccess(0);
    }

    @Override
    public int getPageCount() {
        return 0;
    }

    @Override
    public boolean isPdfPasswordProtected() {
        return false;
    }

}
