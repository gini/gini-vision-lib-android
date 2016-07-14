package net.gini.android.vision.util.promise;

/**
 * @exclude
 */
public class SimplePromise {

    public interface DoneCallback {
        void onDone();
    }

    private DoneCallback mDoneCallback;
    private boolean mDone = false;

    public void then(DoneCallback doneCallback) {
        mDoneCallback = doneCallback;
        if (mDone) {
            triggerDone();
        }
    }

    void triggerDone() {
        mDone = true;
        if (mDoneCallback != null) {
            mDoneCallback.onDone();
        }
    }
}
