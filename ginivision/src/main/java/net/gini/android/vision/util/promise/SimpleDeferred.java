package net.gini.android.vision.util.promise;

/**
 * @exclude
 */
public class SimpleDeferred {

    private SimplePromise mPromise = new SimplePromise();

    public SimplePromise promise() {
        return mPromise;
    }

    public void resolve() {
        mPromise.triggerDone();
    }
}
