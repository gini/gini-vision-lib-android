package net.gini.android.vision.util.promise;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Promises {

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
