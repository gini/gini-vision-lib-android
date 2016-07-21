package net.gini.android.vision.util.promise;

import static com.google.common.truth.Truth.assertThat;

import android.support.annotation.Nullable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(JUnit4.class)
public class SimpleDeferredTest {

    @Test
    public void should_triggerDoneCallback_withAnyObject() {
        final String[] doneResult = new String[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();
        promise.done(new SimplePromise.DoneCallback() {
            @Override
            public SimplePromise onDone(Object result) {
                doneResult[0] = (String) result;
                return null;
            }
        });

        deferred.resolve("Promise result");

        assertThat(doneResult[0]).isEqualTo("Promise result");
    }

    @Test
    public void should_triggerFailCallback_withAnyObject() {
        final CustomObject[] failResult = new CustomObject[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();
        promise.fail(new SimplePromise.FailCallback() {
            @Override
            public SimplePromise onFailed(Object failure) {
                failResult[0] = (CustomObject) failure;
                return null;
            }
        });

        deferred.reject(new CustomObject(Collections.singletonList(42)));

        assertThat(failResult[0].someList).isNotEmpty();
        assertThat(failResult[0].someList.get(0)).isEqualTo(42);
    }

    @Test
    public void should_allowChaining_ofPromises_forDoneCallbacks() {
        final String[] doneResult1 = new String[1];
        final CustomObject[] doneResult2 = new CustomObject[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();

        promise
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult1[0] = (String) result;

                        SimpleDeferred chainedDeferred = new SimpleDeferred();
                        chainedDeferred.resolve(new CustomObject(Collections.singletonList(42)));
                        return chainedDeferred.promise();
                    }
                })
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult2[0] = (CustomObject) result;
                        return null;
                    }
                });

        deferred.resolve("Promise result");

        assertThat(doneResult1[0]).isEqualTo("Promise result");

        assertThat(doneResult2[0].someList).isNotEmpty();
        assertThat(doneResult2[0].someList.get(0)).isEqualTo(42);
    }

    @Test
    public void should_allowChaining_ofDoneCallbacks() {
        final String[] doneResult1 = new String[1];
        final String[] doneResult2 = new String[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();

        promise
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult1[0] = (String) result;
                        return null;
                    }
                })
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult2[0] = (String) result;
                        return null;
                    }
                });

        deferred.resolve("Promise result");

        assertThat(doneResult1[0]).isEqualTo("Promise result");
        assertThat(doneResult2[0]).isEqualTo("Promise result");
    }

    @Test
    public void should_allowChaining_ofPromises_forFailCallbacks() {
        final String[] failure1 = new String[1];
        final CustomObject[] failure2 = new CustomObject[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();

        promise
                .fail(new SimplePromise.FailCallback() {
                    @Override
                    public SimplePromise onFailed(Object failure) {
                        failure1[0] = (String) failure;

                        SimpleDeferred chainedDeferred = new SimpleDeferred();
                        chainedDeferred.reject(new CustomObject(Collections.singletonList(42)));
                        return chainedDeferred.promise();
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Override
                    public SimplePromise onFailed(Object failure) {
                        failure2[0] = (CustomObject) failure;
                        return null;
                    }
                });

        deferred.reject("Promise result");

        assertThat(failure1[0]).isEqualTo("Promise result");

        assertThat(failure2[0].someList).isNotEmpty();
        assertThat(failure2[0].someList.get(0)).isEqualTo(42);
    }

    @Test
    public void should_allowChaining_ofFailCallbacks() {
        final String[] failure1 = new String[1];
        final String[] failure2 = new String[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();

        promise
                .fail(new SimplePromise.FailCallback() {
                    @Override
                    public SimplePromise onFailed(Object failure) {
                        failure1[0] = (String) failure;
                        return null;
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Override
                    public SimplePromise onFailed(Object failure) {
                        failure2[0] = (String) failure;
                        return null;
                    }
                });

        deferred.reject("Promise result");

        assertThat(failure1[0]).isEqualTo("Promise result");
        assertThat(failure2[0]).isEqualTo("Promise result");
    }

    @Test
    public void should_allowChaining_ofPromises_forDoneCallbacks_andFailCallbacks() {
        final String[] doneResult1 = new String[1];
        final CustomObject[] failure2 = new CustomObject[1];
        final CustomObject[] doneResult3 = new CustomObject[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();

        final SimpleDeferred[] chainedDeferred2 = new SimpleDeferred[1];

        promise
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult1[0] = (String) result;

                        SimpleDeferred chainedDeferred1 = new SimpleDeferred();
                        chainedDeferred1.reject(new CustomObject(Collections.singletonList(42)));
                        return chainedDeferred1.promise();
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Override
                    public SimplePromise onFailed(Object failure) {
                        failure2[0] = (CustomObject) failure;

                        chainedDeferred2[0] = new SimpleDeferred();
                        return chainedDeferred2[0].promise();
                    }
                })
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult3[0] = (CustomObject) result;
                        return null;
                    }
                });

        deferred.resolve("Promise result");

        chainedDeferred2[0].resolve(new CustomObject(Collections.singletonList(314)));

        assertThat(doneResult1[0]).isEqualTo("Promise result");

        assertThat(failure2[0].someList).isNotEmpty();
        assertThat(failure2[0].someList.get(0)).isEqualTo(42);

        assertThat(doneResult3[0].someList).isNotEmpty();
        assertThat(doneResult3[0].someList.get(0)).isEqualTo(314);
    }

    @Test
    public void should_propagateFailCallbacks() {
        final String[] doneResult1 = new String[1];
        final String[] failResult1 = new String[1];
        final String[] failResult2 = new String[1];

        SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();

        promise
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult1[0] = (String) result;
                        return null;
                    }
                })
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult1[0] = (String) result;
                        return null;
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Override
                    public SimplePromise onFailed(Object failure) {
                        failResult1[0] = (String) failure;
                        return null;
                    }
                })
                .done(new SimplePromise.DoneCallback() {
                    @Override
                    public SimplePromise onDone(Object result) {
                        doneResult1[0] = (String) result;
                        return null;
                    }
                })
                .fail(new SimplePromise.FailCallback() {
                    @Override
                    public SimplePromise onFailed(Object failure) {
                        failResult2[0] = (String) failure;
                        return null;
                    }
                });

        deferred.reject("Promise failure");

        assertThat(doneResult1[0]).isNull();
        assertThat(failResult1[0]).isEqualTo("Promise failure");
        assertThat(failResult2[0]).isEqualTo("Promise failure");
    }

    @Test
    public void should_triggerDoneCallback_onResolutionThread() throws InterruptedException {
        final String[] resolutionThreadName = new String[1];
        final String[] doneThreadName = new String[1];

        final SimpleDeferred deferred = new SimpleDeferred();
        SimplePromise promise = deferred.promise();

        promise.done(new SimplePromise.DoneCallback() {
            @Nullable
            @Override
            public SimplePromise onDone(@Nullable Object result) {
                doneThreadName[0] = Thread.currentThread().getName();
                return null;
            }
        });

        Runnable resolve = new Runnable() {
            @Override
            public void run() {
                deferred.resolve(null);
                resolutionThreadName[0] = Thread.currentThread().getName();
            }
        };

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(resolve);

        executor.awaitTermination(1, TimeUnit.SECONDS);

        assertThat(resolutionThreadName[0]).isNotNull();
        assertThat(doneThreadName[0]).isNotNull();
        assertThat(doneThreadName[0]).isEqualTo(resolutionThreadName[0]);
    }

    private class CustomObject {
        public final List<Integer> someList;

        private CustomObject(List<Integer> someList) {
            this.someList = someList;
        }
    }
}
