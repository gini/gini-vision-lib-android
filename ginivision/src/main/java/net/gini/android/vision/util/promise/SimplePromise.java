package net.gini.android.vision.util.promise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * <p>
 *     The promise class for a simple promise pattern implementation.
 * </p>
 * <p>
 *     Generics are not used by design. See {@link SimpleDeferred} for details.
 * </p>
 * <p>
 *     Use this to register callbacks to handle the result of a task.
 * </p>
 * <p>
 *     You may return a new promise from a callback by creating a new deferred and returning its promise.
 * </p>
 * @exclude
 */
public class SimplePromise {

    /**
     * <p>
     *     Callback for handling task completion.
     * </p>
     */
    public interface DoneCallback {
        /**
         *<p>
         *     Invoked when the task has been resolved.
         *</p>
         * <p>
         *     Return a new promise when you need to defer another task. Create a new deferred instance and return its promise.
         * </p>
         * @param result the result of the task or null
         * @return a new promise or null
         */
        @Nullable
        SimplePromise onDone(@Nullable Object result);
    }

    /**
     * <p>
     *     Callback for handling task failure.
     * </p>
     */
    public interface FailCallback {
        /**
         *<p>
         *     Invoked when the task has been rejected.
         *</p>
         * <p>
         *     Return a new promise when you need to defer another task. Create a new deferred instance and return its promise.
         * </p>
         * @param failure the failure cause of the task or null
         * @return a new promise or null
         */
        @Nullable
        SimplePromise onFailed(@Nullable Object failure);
    }

    private DoneCallback mDoneCallback;
    private FailCallback mFailCallback;
    private boolean mDone = false;
    private Object mResult;
    private boolean mFailed = false;
    private Object mFailure;

    private SimplePromise mNextPromise;

    SimplePromise() {
    }

    /**
     * <p>
     *     Copy constructor to create a new promise based on the source promise.
     * </p>
     * @param sourcePromise copies this promise
     */
    SimplePromise(@NonNull SimplePromise sourcePromise) {
        this.mDone = sourcePromise.mDone;
        this.mFailed = sourcePromise.mFailed;
        this.mResult = sourcePromise.mResult;
        this.mFailure = sourcePromise.mFailure;
    }

    /**
     * <p>
     *     Register a callback to be notified when the task succeeded.
     * </p>
     * <p>
     *     Invoked on the thread on which the deferred object was resolved.
     * </p>
     * @param doneCallback callback invoked when the task has been resolved
     * @return a promise for adding new callbacks
     */
    @NonNull
    public SimplePromise done(@NonNull DoneCallback doneCallback) {
        mDoneCallback = doneCallback;
        initNextPromise();
        if (mDone) {
            triggerDone(mResult);
        }
        return mNextPromise;
    }

    /**
     * <p>
     *     Register a callback to be notified when the task failed.
     * </p>
     * <p>
     *     Invoked on the thread on which the deferred object was rejected.
     * </p>
     * @param failCallback callback invoked when the task has been rejected
     * @return a promise for adding new callbacks
     */
    @NonNull
    public SimplePromise fail(@NonNull FailCallback failCallback) {
        mFailCallback = failCallback;
        initNextPromise();
        if (mFailed) {
            triggerFail(mFailure);
        }
        return mNextPromise;
    }

    private void initNextPromise() {
        if (mNextPromise == null) {
            mNextPromise = new SimplePromise(this);
        }
    }

    void triggerDone(@Nullable Object result) {
        mDone = true;
        mResult = result;
        SimplePromise chainedPromise = null;
        if (mDoneCallback != null) {
            chainedPromise = mDoneCallback.onDone(result);
        }
        if (chainedPromise != null) {
            chainedPromise
                    .done(new DoneCallback() {
                        @Override
                        public SimplePromise onDone(Object result) {
                            mNextPromise.triggerDone(result);
                            return null;
                        }
                    })
                    .fail(new FailCallback() {
                        @Override
                        public SimplePromise onFailed(Object failure) {
                            mNextPromise.triggerFail(failure);
                            return null;
                        }
                    });
        } else if (mNextPromise != null) {
            mNextPromise.triggerDone(result);
        }
    }

    void triggerFail(@Nullable Object failure) {
        mFailed = true;
        mFailure = failure;
        SimplePromise chainedPromise = null;
        if (mFailCallback != null) {
            chainedPromise = mFailCallback.onFailed(failure);
        }
        if (chainedPromise != null) {
            chainedPromise
                    .done(new DoneCallback() {
                        @Override
                        public SimplePromise onDone(Object result) {
                            mNextPromise.triggerDone(result);
                            return null;
                        }
                    })
                    .fail(new FailCallback() {
                        @Override
                        public SimplePromise onFailed(Object failure) {
                            mNextPromise.triggerFail(failure);
                            return null;
                        }
                    });
        } else if (mNextPromise != null) {
            mNextPromise.triggerFail(failure);
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimplePromise{");
        sb.append("mDoneCallback=").append(mDoneCallback);
        sb.append(", mFailCallback=").append(mFailCallback);
        sb.append(", mDone=").append(mDone);
        sb.append(", mResult=").append(mResult);
        sb.append(", mFailed=").append(mFailed);
        sb.append(", mFailure=").append(mFailure);
        sb.append(", mNextPromise=").append(mNextPromise);
        sb.append("}");
        return sb.toString();
    }
}
