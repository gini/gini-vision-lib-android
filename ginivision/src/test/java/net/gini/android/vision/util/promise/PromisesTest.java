package net.gini.android.vision.util.promise;

import static com.google.common.truth.Truth.assertThat;

import android.support.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RunWith(JUnit4.class)
public class PromisesTest {

    @Test
    public void should_bundlePromises_andReturnAllResolutionResults_andFailureResults() {
        SimpleDeferred deferredResolved1 = new SimpleDeferred();
        SimpleDeferred deferredResolved2 = new SimpleDeferred();
        SimpleDeferred deferredResolved3 = new SimpleDeferred();
        SimpleDeferred deferredFailed1 = new SimpleDeferred();
        SimpleDeferred deferredFailed2 = new SimpleDeferred();

        final AtomicReference<List<Promises.Resolution>> resolutionResults = new AtomicReference<>();
        final AtomicReference<List<Promises.Failure>> failureResults = new AtomicReference<>();

        Promises.bundle(deferredResolved1.promise(),
                deferredFailed1.promise(),
                deferredResolved2.promise(),
                deferredFailed2.promise(),
                deferredResolved3.promise())
                .done(new SimplePromise.DoneCallback() {
                    @Nullable
                    @Override
                    public SimplePromise onDone(@Nullable Object result) {
                        resolutionResults.set((List<Promises.Resolution>) result);
                        return null;
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Nullable
                    @Override
                    public SimplePromise onFailed(@Nullable Object failure) {
                        failureResults.set((List<Promises.Failure>) failure);
                        return null;
                    }
                });

        String resolution1 = "Task completed";
        int resolution2 = 42;
        CustomObject resolution3 = new CustomObject(Collections.singletonList(314));
        String failed1 = "Task failed";
        CustomObject failed2 = new CustomObject(Arrays.asList(271, 300));

        deferredResolved1.resolve(resolution1);
        deferredResolved2.resolve(resolution2);
        deferredResolved3.resolve(resolution3);
        deferredFailed1.reject(failed1);
        deferredFailed2.reject(failed2);

        List<Promises.Resolution> doneList = resolutionResults.get();
        List<Promises.Failure> failedList = failureResults.get();
        assertThat(doneList).isNotNull();
        assertThat(failedList).isNotNull();

        assertThat(doneList.get(0).getResult()).isEqualTo(resolution1);
        assertThat(doneList.get(2).getResult()).isEqualTo(resolution2);
        assertThat(doneList.get(4).getResult()).isEqualTo(resolution3);

        assertThat(failedList.get(1).getFailure()).isEqualTo(failed1);
        assertThat(failedList.get(3).getFailure()).isEqualTo(failed2);
    }

    @Test
    public void should_returnInOrder_allResolutionResults_andFailureResults() {
        SimpleDeferred deferredResolved1 = new SimpleDeferred();
        SimpleDeferred deferredResolved2 = new SimpleDeferred();
        SimpleDeferred deferredFailed1 = new SimpleDeferred();
        SimpleDeferred deferredFailed2 = new SimpleDeferred();

        final AtomicReference<List<Promises.Resolution>> resolutionResults = new AtomicReference<>();
        final AtomicReference<List<Promises.Failure>> failureResults = new AtomicReference<>();

        Promises.bundle(deferredResolved1.promise(),
                deferredFailed1.promise(),
                deferredResolved2.promise(),
                deferredFailed2.promise())
                .done(new SimplePromise.DoneCallback() {
                    @Nullable
                    @Override
                    public SimplePromise onDone(@Nullable Object result) {
                        resolutionResults.set((List<Promises.Resolution>) result);
                        return null;
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Nullable
                    @Override
                    public SimplePromise onFailed(@Nullable Object failure) {
                        failureResults.set((List<Promises.Failure>) failure);
                        return null;
                    }
                });

        String resolution1 = "Task completed";
        int resolution2 = 42;
        String failed1 = "Task failed";
        CustomObject failed2 = new CustomObject(Arrays.asList(271, 300));

        deferredFailed2.reject(failed2);
        deferredResolved2.resolve(resolution2);
        deferredResolved1.resolve(resolution1);
        deferredFailed1.reject(failed1);

        List<Promises.Resolution> doneList = resolutionResults.get();
        List<Promises.Failure> failedList = failureResults.get();
        assertThat(doneList).isNotNull();
        assertThat(failedList).isNotNull();

        assertThat(doneList.get(0).getResult()).isEqualTo(resolution1);
        assertThat(doneList.get(2).getResult()).isEqualTo(resolution2);

        assertThat(failedList.get(1).getFailure()).isEqualTo(failed1);
        assertThat(failedList.get(3).getFailure()).isEqualTo(failed2);
    }

    @Test
    public void should_notTriggerCallbacks_whenSomeDeferred_wereNotResolved() {
        SimpleDeferred deferredResolved1 = new SimpleDeferred();
        SimpleDeferred deferredResolved2 = new SimpleDeferred();

        final AtomicInteger doneCallbackInvocations = new AtomicInteger();

        Promises.bundle(deferredResolved1.promise(),
                deferredResolved2.promise())
                .done(new SimplePromise.DoneCallback() {
                    @Nullable
                    @Override
                    public SimplePromise onDone(@Nullable Object result) {
                        doneCallbackInvocations.incrementAndGet();
                        return null;
                    }
                });

        deferredResolved1.resolve(null);
        deferredResolved2.resolve(null);

        assertThat(doneCallbackInvocations.get()).isEqualTo(1);

        deferredResolved1.resolve(null);

        assertThat(doneCallbackInvocations.get()).isEqualTo(1);
    }

    @Test
    public void should_retriggerRelevantCallbacks_whenDeferred_areResolved_orRejected() {
        SimpleDeferred deferred1 = new SimpleDeferred();
        SimpleDeferred deferred2 = new SimpleDeferred();
        SimpleDeferred deferred3 = new SimpleDeferred();

        final AtomicInteger doneCallbackInvocations = new AtomicInteger();
        final AtomicInteger failCallbackInvocations = new AtomicInteger();

        Promises.bundle(deferred1.promise(),
                deferred2.promise(),
                deferred3.promise())
                .done(new SimplePromise.DoneCallback() {
                    @Nullable
                    @Override
                    public SimplePromise onDone(@Nullable Object result) {
                        doneCallbackInvocations.incrementAndGet();
                        return null;
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Nullable
                    @Override
                    public SimplePromise onFailed(@Nullable Object failure) {
                        failCallbackInvocations.incrementAndGet();
                        return null;
                    }
                });

        deferred1.resolve(null);
        deferred2.reject(null);
        deferred3.reject(null);

        assertThat(doneCallbackInvocations.get()).isEqualTo(1);
        assertThat(failCallbackInvocations.get()).isEqualTo(1);

        deferred1.resolve(null);
        deferred2.resolve(null);
        deferred3.reject(null);

        assertThat(doneCallbackInvocations.get()).isEqualTo(2);
        assertThat(failCallbackInvocations.get()).isEqualTo(2);

        deferred1.resolve(null);
        deferred2.resolve(null);
        deferred3.resolve(null);

        assertThat(doneCallbackInvocations.get()).isEqualTo(3);
        assertThat(failCallbackInvocations.get()).isEqualTo(2);

        deferred1.reject(null);
        deferred2.reject(null);
        deferred3.reject(null);

        assertThat(doneCallbackInvocations.get()).isEqualTo(3);
        assertThat(failCallbackInvocations.get()).isEqualTo(3);
    }
}
