package net.gini.android.vision.internal.document;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.LruCache;

import net.gini.android.vision.document.GiniVisionDocument;
import net.gini.android.vision.internal.AsyncCallback;

/**
 * Created by Alpar Szotyori on 16.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class DocumentDataMemoryCache {

    private final LruCache<GiniVisionDocument, byte[]> mDataCache;

    public DocumentDataMemoryCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mDataCache = new LruCache<GiniVisionDocument, byte[]>(cacheSize) {
            @Override
            protected int sizeOf(final GiniVisionDocument key, final byte[] value) {
                return value.length / 1024;
            }

            @Override
            protected void entryRemoved(final boolean evicted, final GiniVisionDocument key,
                    final byte[] oldValue,
                    final byte[] newValue) {
                if (newValue == null) {
                    key.unloadData();
                }
            }
        };
    }

    // WIP-MM: queue for loading data
    public void getData(@NonNull final Context context, @NonNull final GiniVisionDocument document,
            @NonNull final AsyncCallback<byte[]> callback) {
        if (mDataCache.get(document) != null) {
            callback.onSuccess(mDataCache.get(document));
            return;
        }
        document.loadData(context, new AsyncCallback<byte[]>() {
            @Override
            public void onSuccess(final byte[] result) {
                mDataCache.put(document, result);
                callback.onSuccess(result);
            }

            @Override
            public void onError(final Exception exception) {
                callback.onError(exception);
            }
        });
    }

    // WIP-MM: invalidate after photo was altered, document data was updated and written to disk
    public void invalidateData(@NonNull final GiniVisionDocument document) {
        mDataCache.remove(document);
    }

    public void clear() {
        mDataCache.evictAll();
    }

}
