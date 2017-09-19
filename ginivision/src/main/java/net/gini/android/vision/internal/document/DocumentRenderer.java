package net.gini.android.vision.internal.document;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
public interface DocumentRenderer {
    @Nullable
    Bitmap toBitmap(@NonNull final Size targetSize);

    int getRotationForDisplay();
}
