package net.gini.android.vision.internal.camera.api;

import android.os.Handler;
import android.os.Looper;

/**
 * Internal use only.
 *
 * @suppress
 */
public class UIExecutor {
    private final Thread mUiThread;
    private final Handler mUiHandler;

    public UIExecutor() {
        final Looper mainLooper = Looper.getMainLooper();
        mUiThread = mainLooper.getThread();
        mUiHandler = new Handler(mainLooper);
    }

    /**
     * Runs the specified action on the UI thread. If the current thread is the UI thread, then the
     * action is executed immediately. If the current thread is not the UI thread, the action is
     * posted to the event queue of the UI thread.
     *
     * @param action the action to run on the UI thread
     */
    public void runOnUiThread(final Runnable action) {
        if (Thread.currentThread() != mUiThread) {
            mUiHandler.post(action);
        } else {
            action.run();
        }
    }
}