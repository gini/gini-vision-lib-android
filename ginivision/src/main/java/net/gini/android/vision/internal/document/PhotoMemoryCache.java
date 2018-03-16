package net.gini.android.vision.internal.document;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.LruCache;

import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactoryDocumentAsyncTask;

/**
 * Created by Alpar Szotyori on 16.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public class PhotoMemoryCache {

    private final LruCache<ImageDocument, Photo> mPhotoCache;
    private final DocumentDataMemoryCache mDocumentDataMemoryCache;

    public PhotoMemoryCache(@NonNull final DocumentDataMemoryCache documentDataMemoryCache) {
        mDocumentDataMemoryCache = documentDataMemoryCache;
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        mPhotoCache = new LruCache<ImageDocument, Photo>(cacheSize) {
            @Override
            protected int sizeOf(final ImageDocument key, final Photo value) {
                return value.getBitmapPreview().getByteCount() / 1024;
            }
        };
    }

    // WIP-MM: queue for creating photos
    public void getPhoto(@NonNull final Context context, @NonNull final ImageDocument document,
            @NonNull final AsyncCallback<Photo> callback) {
        if (mPhotoCache.get(document) != null) {
            callback.onSuccess(mPhotoCache.get(document));
            return;
        }
        mDocumentDataMemoryCache.getData(context, document, new AsyncCallback<byte[]>() {
            @Override
            public void onSuccess(final byte[] result) {
                final PhotoFactoryDocumentAsyncTask asyncTask = new PhotoFactoryDocumentAsyncTask(
                        new AsyncCallback<Photo>() {
                            @Override
                            public void onSuccess(final Photo result) {
                                mPhotoCache.put(document, result);
                                callback.onSuccess(result);
                            }

                            @Override
                            public void onError(final Exception exception) {
                                callback.onError(exception);
                            }
                        });
                asyncTask.execute(document);
            }

            @Override
            public void onError(final Exception exception) {
                callback.onError(exception);
            }
        });
    }

    public void clear() {
        mPhotoCache.evictAll();
    }

    public void invalidatePhoto(@NonNull final ImageDocument document) {
        mPhotoCache.remove(document);
    }
}
