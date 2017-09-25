package net.gini.android.vision.document;

import android.support.annotation.NonNull;

/**
 * @exclude
 */
public interface LoadDataCallback {
    void onDataLoaded();

    void onError(@NonNull final Exception exception);
}
