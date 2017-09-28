package net.gini.android.vision.internal.pdf;

import android.support.annotation.NonNull;

import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
class RendererPreLollipop implements Renderer {

    @Override
    public void toBitmap(@NonNull final Size targetSize,
            @NonNull final Callback callback) {
        callback.onBitmapReady(null);
    }
}
