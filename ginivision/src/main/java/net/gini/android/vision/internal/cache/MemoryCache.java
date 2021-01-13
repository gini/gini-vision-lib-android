package net.gini.android.vision.internal.cache;

import android.content.Context;
import android.util.LruCache;

import net.gini.android.vision.AsyncCallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.NOPLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 21.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public abstract class MemoryCache<K, V> {

    private static final boolean DEBUG = false;
    private final Logger mLog; // NOPMD
    private final LruCache<K, V> mCache;
    private final Queue<Worker<K, V>> mWorkerQueue = new LinkedList<>();
    private final List<Worker<K, V>> mRunningWorkers;
    private final int mRunningWorkersLimit;
    private final Map<K, List<AsyncCallback<V, Exception>>> mWaitingCallbacks = new HashMap<>();

    MemoryCache(final int runningWorkersLimit) {
        mRunningWorkersLimit = runningWorkersLimit;
        mRunningWorkers = new ArrayList<>(runningWorkersLimit);
        mCache = createCache();
        if (DEBUG) {
            mLog = LoggerFactory.getLogger(getClass());
        } else {
            mLog = NOPLogger.NOP_LOGGER;
        }
    }

    protected abstract LruCache<K, V> createCache();

    public void get(@NonNull final Context context, @NonNull final K key,
            @NonNull final AsyncCallback<V, Exception> callback) {
        mLog.debug("Get for key {}", getNameForLog(key));
        if (mCache.get(key) != null) {
            final V value = mCache.get(key);
            mLog.debug("Return cached {}", getNameForLog(value));
            callback.onSuccess(value);
            return;
        }

        mLog.debug("Create worker");
        final Worker<K, V> worker = createWorker(mRunningWorkers, key,
                new AsyncCallback<V, Exception>() {
                    @Override
                    public void onSuccess(final V result) {
                        mLog.debug("Worker finished with result {}", getNameForLog(result));
                        mCache.put(key, result);
                        callOnSuccessForWaitingCallbacks(key, result);
                        mLog.debug("Remove callbacks for key {}", getNameForLog(key));
                        mWaitingCallbacks.remove(key);
                        executeNextWorker(context);
                    }

                    @Override
                    public void onError(final Exception exception) {
                        mLog.error("Worker finished with error", exception);
                        callOnErrorForWaitingCallbacks(key, exception);
                        mLog.debug("Remove callbacks for key {}", getNameForLog(key));
                        mWaitingCallbacks.remove(key);
                        executeNextWorker(context);
                    }

                    @Override
                    public void onCancelled() {
                        mLog.error("Worker was cancelled");
                        callOnCancelledForWaitingCallbacks(key);
                        mLog.debug("Remove callbacks for key {}", getNameForLog(key));
                        mWaitingCallbacks.remove(key);
                        executeNextWorker(context);
                    }
                });

        List<AsyncCallback<V, Exception>> callbacks = mWaitingCallbacks.get(key);
        if (callbacks == null) {
            mLog.debug("First callback {} registered for key {}", getNameForLog(callback),
                    getNameForLog(key));
            callbacks = new ArrayList<>();
            callbacks.add(callback);
            mWaitingCallbacks.put(key, callbacks);
        } else {
            mLog.debug("Additional callback {} registered for key {}", getNameForLog(callback),
                    getNameForLog(key));
            callbacks.add(callback);
        }

        mLog.debug("Schedule worker for key {}", getNameForLog(key));
        if (!mRunningWorkers.contains(worker) && mRunningWorkers.size() < mRunningWorkersLimit) {
            mLog.debug("Execute worker for key {}", getNameForLog(key));
            mRunningWorkers.add(worker);
            worker.execute(context);
        } else if (!mWorkerQueue.contains(worker)) {
            mLog.debug("Queue worker for key {}", getNameForLog(key));
            mWorkerQueue.add(worker);
        } else {
            mLog.debug("Worker already queued for key {}", getNameForLog(key));
        }
    }

    private void callOnSuccessForWaitingCallbacks(final K key, final V result) {
        final List<AsyncCallback<V, Exception>> callbacks = mWaitingCallbacks.get(key);
        if (callbacks != null) {
            for (final AsyncCallback<V, Exception> waitingCallback : callbacks) {
                mLog.debug("Invoke callback {} for key {}",
                        getNameForLog(waitingCallback), getNameForLog(key));
                waitingCallback.onSuccess(result);
            }
        }
    }

    private void callOnErrorForWaitingCallbacks(final K key, final Exception exception) {
        final List<AsyncCallback<V, Exception>> callbacks = mWaitingCallbacks.get(key);
        if (callbacks != null) {
            for (final AsyncCallback<V, Exception> waitingCallback : callbacks) {
                mLog.debug("Invoke callback {} for key {}",
                        getNameForLog(waitingCallback), getNameForLog(key));
                waitingCallback.onError(exception);
            }
        }
    }

    private void callOnCancelledForWaitingCallbacks(final K key) {
        final List<AsyncCallback<V, Exception>> callbacks = mWaitingCallbacks.get(key);
        if (callbacks != null) {
            for (final AsyncCallback<V, Exception> waitingCallback : callbacks) {
                mLog.debug("Invoke callback {} for key {}",
                        getNameForLog(waitingCallback), getNameForLog(key));
                waitingCallback.onCancelled();
            }
        }
    }

    private <T> String getNameForLog(final T object) {
        return String.format(Locale.US, "%s[%d]", object.getClass().getSimpleName(),
                object.hashCode());
    }

    protected abstract Worker<K, V> createWorker(
            @NonNull final List<Worker<K, V>> runningWorkers,
            @NonNull final K subject,
            @NonNull final AsyncCallback<V, Exception> callback);


    private void executeNextWorker(@NonNull final Context context) {
        mLog.debug("Execute next worker");
        final Worker worker = mWorkerQueue.peek();
        if (worker != null) {
            if (!mRunningWorkers.contains(worker)
                    && mRunningWorkers.size() < mRunningWorkersLimit) {
                mRunningWorkers.add(worker);
                mWorkerQueue.poll();
                mLog.debug("Execute queued worker for key {}", getNameForLog(worker.getSubject()));
                worker.execute(context);
            } else {
                mLog.debug("Cannot execute worker for key {}. Running worker limit reached.",
                        getNameForLog(worker.getSubject()));
            }
        } else {
            mLog.debug("No queued workers");
            if (mRunningWorkers.isEmpty() && !mWaitingCallbacks.isEmpty()) {
                mLog.error("{} dangling callbacks", mWaitingCallbacks.size());
                if (DEBUG) {
                    logDanglingCallbacks();
                }
            }
        }
    }

    private void logDanglingCallbacks() {
        for (final Map.Entry<K, List<AsyncCallback<V, Exception>>> entry
                : mWaitingCallbacks.entrySet()) {
            for (final AsyncCallback<V, Exception> waitingCallback : entry.getValue()) {
                mLog.error("Dangling callback {} for key {}", getNameForLog(waitingCallback),
                        getNameForLog(entry.getKey()));
            }
        }
    }

    public void invalidate(@NonNull final K key) {
        mCache.remove(key);
    }

    public void clear() {
        mCache.evictAll();
    }

    protected abstract static class Worker<S, V> {

        private final Logger mLog; // NOPMD
        private final List<Worker<S, V>> mRunningWorkers;
        private final S mSubject;
        private final AsyncCallback<V, Exception> mCallback;

        Worker(@NonNull final List<Worker<S, V>> runningWorkers,
                @NonNull final S subject,
                @NonNull final AsyncCallback<V, Exception> callback) {
            mRunningWorkers = runningWorkers;
            mSubject = subject;
            mCallback = callback;
            if (DEBUG) {
                mLog = LoggerFactory.getLogger(getClass());
            } else {
                mLog = NOPLogger.NOP_LOGGER;
            }
        }

        public S getSubject() {
            return mSubject;
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
            mLog.debug("Execute");
            doExecute(context, mSubject, new AsyncCallback<V, Exception>() {
                @Override
                public void onSuccess(final V result) {
                    mLog.debug("Succeded");
                    mLog.debug("Remove self from running workers");
                    mRunningWorkers.remove(Worker.this);
                    mCallback.onSuccess(result);
                }

                @Override
                public void onError(final Exception exception) {
                    mLog.debug("Failed");
                    mLog.debug("Remove self from running workers");
                    mRunningWorkers.remove(Worker.this);
                    mCallback.onError(exception);
                }

                @Override
                public void onCancelled() {
                    mLog.debug("Cancelled");
                    mLog.debug("Remove self from running workers");
                    mRunningWorkers.remove(Worker.this);
                    mCallback.onCancelled();
                }
            });
        }

        protected abstract void doExecute(@NonNull final Context context, @NonNull final S subject,
                @NonNull final AsyncCallback<V, Exception> callback);
    }
}
