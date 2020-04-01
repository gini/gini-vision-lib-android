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
public interface Renderer {

    void toBitmap(@NonNull final Size targetSize,
            @NonNull final AsyncCallback<Bitmap, Exception> asyncCallback);

    void getPageCount(@NonNull final AsyncCallback<Integer, Exception> asyncCallback);

    int getPageCount();

    boolean isPdfPasswordProtected();
}
