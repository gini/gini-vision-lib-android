package net.gini.android.vision.internal.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactoryDocumentAsyncTask;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 16.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public class PhotoMemoryCache extends MemoryCache<ImageDocument, Photo> {

    private static final int RUNNING_WORKERS_LIMIT = 3;
    private final DocumentDataMemoryCache mDocumentDataMemoryCache;

    public PhotoMemoryCache(@NonNull final DocumentDataMemoryCache documentDataMemoryCache) {
        super(RUNNING_WORKERS_LIMIT);
        mDocumentDataMemoryCache = documentDataMemoryCache;
    }

    @Override
    protected LruCache<ImageDocument, Photo> createCache() {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;
        return new LruCache<ImageDocument, Photo>(cacheSize) {
            @Override
            protected int sizeOf(final ImageDocument key, final Photo value) {
                final Bitmap preview = value.getBitmapPreview();
                if (preview != null) {
                    return preview.getByteCount() / 1024;
                }
                return 1;
            }
        };
    }

    @Override
    protected Worker<ImageDocument, Photo> createWorker(
            @NonNull final List<Worker<ImageDocument, Photo>> runningWorkers,
            @NonNull final ImageDocument subject,
            @NonNull final AsyncCallback<Photo, Exception> callback) {
        return new PhotoWorker(runningWorkers, subject, mDocumentDataMemoryCache,
                new AsyncCallback<Photo, Exception>() {
                    @Override
                    public void onSuccess(final Photo result) {
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onError(final Exception exception) {
                        callback.onError(exception);
                    }

                    @Override
                    public void onCancelled() {
                        callback.onCancelled();
                    }
                });
    }

    private static class PhotoWorker extends MemoryCache.Worker<ImageDocument, Photo> {

        private final DocumentDataMemoryCache mDocumentDataMemoryCache;

        private PhotoWorker(
                @NonNull final List<Worker<ImageDocument, Photo>> runningWorkers,
                @NonNull final ImageDocument subject,
                @NonNull final DocumentDataMemoryCache documentDataMemoryCache,
                @NonNull final AsyncCallback<Photo, Exception> callback) {
            super(runningWorkers, subject, callback);
            mDocumentDataMemoryCache = documentDataMemoryCache;
        }

        @Override
        protected void doExecute(@NonNull final Context context,
                @NonNull final ImageDocument subject,
                @NonNull final AsyncCallback<Photo, Exception> callback) {
            mDocumentDataMemoryCache.get(context, subject, new AsyncCallback<byte[], Exception>() {
                @Override
                public void onSuccess(final byte[] result) {
                    final PhotoFactoryDocumentAsyncTask asyncTask =
                            new PhotoFactoryDocumentAsyncTask(
                                    new AsyncCallback<Photo, Exception>() {
                                        @Override
                                        public void onSuccess(final Photo result) {
                                            callback.onSuccess(result);
                                        }

                                        @Override
                                        public void onError(final Exception exception) {
                                            callback.onError(exception);
                                        }

                                        @Override
                                        public void onCancelled() {
                                            callback.onCancelled();
                                        }
                                    });
                    asyncTask.execute(subject);
                }

                @Override
                public void onError(final Exception exception) {
                    callback.onError(exception);
                }

                @Override
                public void onCancelled() {
                    callback.onCancelled();
                }
            });
        }
    }
}
