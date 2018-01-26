package net.gini.android.vision.internal.pdf;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
class RendererPreLollipop implements Renderer {

    @Override
    public void toBitmap(@NonNull final Size targetSize,
            @NonNull final AsyncCallback<Bitmap> asyncCallback) {
        asyncCallback.onSuccess(null);
    }

    @Override
    public void getPageCount(@NonNull final AsyncCallback<Integer> asyncCallback) {
        asyncCallback.onSuccess(0);
    }

    @Override
    public int getPageCount() {
        return 0;
    }

}
