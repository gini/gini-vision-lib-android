package net.gini.android.vision.internal.pdf;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
public interface Renderer {
    void toBitmap(@NonNull final Size targetSize, @NonNull final Callback callback);

    int getPageCount();

    interface Callback {
        void onBitmapReady(@Nullable final Bitmap bitmap);
    }
}
