package net.gini.android.vision.util.promise;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * <p>
 *     The deferred class for a simple promise pattern implementation.
 * </p>
 * <p>
 *     Use this to defer the providing of the result of a task by returning a promise using {@link SimpleDeferred#promise()}.
 * </p>
 * <p>
 *     To provide a result call the {@link SimpleDeferred#resolve(Object)} or {@link SimpleDeferred#reject(Object)}.
 * </p>
 * <p>
 *     You should not publish instances of this class. Publish the promise instead.
 * </p>
 <p>
 *     <b>Important</b>: Generics are not used by design to keep the implementation's api similar to a loosely typed language's implementation. This makes chaining of deferred tasks easier, but developers need to document the types returned in done and failure callbacks. They also have take care when casting the callback results. We sacrificed static type checking for flexibility.
 * </p>
 * @exclude
 */
public class SimpleDeferred {

    private final SimplePromise mPromise = new SimplePromise();

    /**
     * <p>
     *     Use the returned promise for registering callbacks to received the result of a task.
     * </p>
     * @return a promise
     */
    @NonNull
    public SimplePromise promise() {
        return mPromise;
    }

    /**
     * <p>
     *     Resolves the task and notifies promise callbacks that the task is done.
     * </p>
     * <p>
     *     The promise's done callbacks are invoked on the thread in which this method is invoked.
     * </p>
     * @param result the result of a task or null
     */
    public void resolve(@Nullable Object result) {
        mPromise.triggerDone(result);
    }

    /**
     * <p>
     *     Rejects the task and notifies promise callbacks that the task has failed.
     * </p>
     * <p>
     *     The promise's fail callbacks are invoked on the thread in which this method is invoked.
     * </p>
     * @param failure the failure cause of a task or null
     */
    public void reject(@Nullable Object failure) {
        mPromise.triggerFail(failure);
    }
}
