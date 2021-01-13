package net.gini.android.vision.internal.document;

import android.content.Context;
import android.graphics.Bitmap;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.internal.util.Size;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * @suppress
 */
public interface DocumentRenderer {

    void toBitmap(@NonNull final Context context,
            @NonNull final Size targetSize, @NonNull final Callback callback);

    void getPageCount(@NonNull final Context context,
            @NonNull final AsyncCallback<Integer, Exception> asyncCallback);

    /**
     * Internal use only.
     *
     * @suppress
     */
    interface Callback {
        void onBitmapReady(@Nullable final Bitmap bitmap, final int rotationForDisplay);
    }
}
