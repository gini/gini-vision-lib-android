package net.gini.android.vision.util.promise;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * <p>
 *     Various helpers for promises.
 * </p>
 */
public class Promises {

    /**
     * <p>
     *     Combine multiple {@link SimplePromise}s. This is useful, if some work needs to be delayed until multiple promises have been completed (resolved or rejected).
     * </p>
     * <p>
     *     The returned promise is resolved after all the bundled promises completed and at least one was resolved. To find out which were resolved cast the result to a {@code List<Promises.Resolution>} and check which indexes are not {@code null}. Indexes correspond to the order the promises were listed in the arguments.
     * </p>
     * <p>
     *     The returned promise is rejected after all the bundled promises completed and at least one was rejected. To find out which were rejected cast the result to a {@code List<Promises.Failure>} and check which indexes are not {@code null}. Indexes correspond to the order the promises were listed in the arguments.
     * </p>
     * <p>
     *     Resolutions and rejections are not exclusive. If some promises were resolved and some rejected the returned promise is both resolved and rejected. Check the returned result after casting to the required class to find out which promises resolved and which failed.
     * </p>
     * <p>
     *     The returned promise is resolved or rejected again only if all the promises were completed again.
     * </p>
     * @param promises {@link SimplePromise}s to observe
     * @return a {@link SimplePromise}{@code [done: List<Promises.Resolution>, fail: List<Promises.Failure]}
     */
    public static SimplePromise bundle(SimplePromise... promises) {
        final SimpleDeferred deferred = new SimpleDeferred();

        final AtomicInteger finishedCounter = new AtomicInteger();
        final int promisesCount = promises.length;

        final AtomicReferenceArray<Object> results = new AtomicReferenceArray<>(promisesCount);
        final AtomicReferenceArray<Object> failures = new AtomicReferenceArray<>(promisesCount);

        for (int i = 0; i < promisesCount; i++) {
            SimplePromise promise = promises[i];
            final int index = i;
            promise
                    .done(new SimplePromise.DoneCallback() {
                        @Nullable
                        @Override
                        public SimplePromise onDone(@Nullable Object result) {
                            results.set(index, new Resolution(result));
                            int finishedPromises = finishedCounter.incrementAndGet();
                            if (finishedPromises == promisesCount) {
                                finishedCounter.set(0);
                                if (hasNonNull(results)) {
                                    deferred.resolve(atomicReferenceArrayToList(results));
                                    clearArray(results);
                                }
                                if (hasNonNull(failures)) {
                                    deferred.reject(atomicReferenceArrayToList(failures));
                                    clearArray(failures);
                                }
                            }
                            return null;
                        }
                    })
                    .fail(new SimplePromise.FailCallback() {
                        @Nullable
                        @Override
                        public SimplePromise onFailed(@Nullable Object failure) {
                            failures.set(index, new Failure(failure));
                            int finishedPromises = finishedCounter.incrementAndGet();
                            if (finishedPromises == promisesCount) {
                                finishedCounter.set(0);
                                if (hasNonNull(results)) {
                                    deferred.resolve(atomicReferenceArrayToList(results));
                                    clearArray(results);
                                }
                                if (hasNonNull(failures)) {
                                    deferred.reject(atomicReferenceArrayToList(failures));
                                    clearArray(failures);
                                }
                            }
                            return null;
                        }
                    });
        }
        return deferred.promise();
    }

    private static List<Object> atomicReferenceArrayToList(AtomicReferenceArray<Object> atomicArray) {
        List<Object> list = new ArrayList<>(atomicArray.length());
        for (int i = 0; i < atomicArray.length(); i++) {
            list.add(atomicArray.get(i));
        }
        return list;
    }

    private static boolean hasNonNull(AtomicReferenceArray<Object> atomicArray) {
        for (int i = 0; i < atomicArray.length(); i++) {
            if (atomicArray.get(i) != null) {
                return true;
            }
        }
        return false;
    }

    private static void clearArray(AtomicReferenceArray<Object> atomicArray) {
        for (int i = 0; i < atomicArray.length(); i++) {
            atomicArray.set(i, null);
        }
    }

    public static class Resolution {

        private final Object mResult;

        public Resolution(@Nullable Object result) {
            this.mResult = result;
        }

        @Nullable
        public Object getResult() {
            return mResult;
        }
    }

    public static class Failure {

        private final Object mFailure;

        public Failure(@Nullable Object failure) {
            this.mFailure = failure;
        }

        @Nullable
        public Object getFailure() {
            return mFailure;
        }
    }
}
