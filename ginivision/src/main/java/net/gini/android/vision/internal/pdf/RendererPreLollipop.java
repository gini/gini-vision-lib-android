package net.gini.android.vision.internal.pdf;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
class RendererPreLollipop implements Renderer {

    @Nullable
    @Override
    public Bitmap toBitmap(@NonNull final Size targetSize) {
        return null;
    }
}
