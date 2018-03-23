package net.gini.android.vision.internal.cache;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.LruCache;

import net.gini.android.vision.internal.AsyncCallback;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Alpar Szotyori on 21.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

public abstract class MemoryCache<K, V> {

    private final LruCache<K, V> mCache;
    private final Queue<Worker<K, V>> mWorkerQueue = new LinkedList<>();
    private final List<Worker<K, V>> mRunningWorkers;
    private final int mRunningWorkersLimit;

    public MemoryCache(final int runningWorkersLimit) {
        mRunningWorkersLimit = runningWorkersLimit;
        mRunningWorkers = new ArrayList<>(runningWorkersLimit);
        mCache = createCache();
    }

    protected abstract LruCache<K, V> createCache();

    public void get(@NonNull final Context context, @NonNull final K key, @NonNull final
    AsyncCallback<V> callback) {
        if (mCache.get(key) != null) {
            callback.onSuccess(mCache.get(key));
            return;
        }

        final Worker worker = createWorker(mRunningWorkers, key,
                new AsyncCallback<V>() {
                    @Override
                    public void onSuccess(final V result) {
                        mCache.put(key, result);
                        callback.onSuccess(result);
                        executeNextWorker(context);
                    }

                    @Override
                    public void onError(final Exception exception) {
                        callback.onError(exception);
                        executeNextWorker(context);
                    }
                });

        if (!mRunningWorkers.contains(worker) && mRunningWorkers.size() < mRunningWorkersLimit) {
            mRunningWorkers.add(worker);
            worker.execute(context);
        } else if (!mWorkerQueue.contains(worker)) {
            mWorkerQueue.add(worker);
        }
    }

    protected abstract Worker<K, V> createWorker(
            @NonNull final List<Worker<K, V>> runningWorkers,
            @NonNull final K subject,
            @NonNull final AsyncCallback<V> callback);


    private void executeNextWorker(@NonNull final Context context) {
        final Worker worker = mWorkerQueue.peek();
        if (worker != null) {
            if (!mRunningWorkers.contains(worker)
                    && mRunningWorkers.size() < mRunningWorkersLimit) {
                mRunningWorkers.add(worker);
                mWorkerQueue.poll();
                worker.execute(context);
            }
        }
    }

    public void invalidate(@NonNull final K key) {
        mCache.remove(key);
    }

    public void clear() {
        mCache.evictAll();
    }

    protected static abstract class Worker<S, V> {

        private final List<Worker<S, V>> mRunningWorkers;
        private final S mSubject;
        private final AsyncCallback<V> mCallback;

        protected Worker(@NonNull final List<Worker<S, V>> runningWorkers,
                @NonNull final S subject,
                @NonNull final AsyncCallback<V> callback) {
            mRunningWorkers = runningWorkers;
            mSubject = subject;
            mCallback = callback;
        }

        @Override
        public int hashCode() {
            return mSubject.hashCode();
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final Worker worker = (Worker) o;

            return mSubject.equals(worker.mSubject);
        }

        public void execute(@NonNull final Context context) {
            doExecute(context, mSubject, new AsyncCallback<V>() {
                @Override
                public void onSuccess(final V result) {
                    mRunningWorkers.remove(Worker.this);
                    mCallback.onSuccess(result);
                }

                @Override
                public void onError(final Exception exception) {
                    mRunningWorkers.remove(Worker.this);
                    mCallback.onError(exception);
                }
            });
        }

        protected abstract void doExecute(@NonNull final Context context, @NonNull final S subject,
                @NonNull final AsyncCallback<V> callback);
    }
}
