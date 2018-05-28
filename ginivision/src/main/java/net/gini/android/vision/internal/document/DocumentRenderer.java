package net.gini.android.vision.internal.document;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
public interface DocumentRenderer {

    void toBitmap(@NonNull final Context context, @NonNull final Size targetSize, @NonNull final Callback callback);

    void getPageCount(@NonNull final Context context, @NonNull final AsyncCallback<Integer> asyncCallback);

    /**
     * @exclude
     */
    interface Callback {
        void onBitmapReady(@Nullable final Bitmap bitmap, final int rotationForDisplay);
    }
}
