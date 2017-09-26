package net.gini.android.vision.internal;

/**
 * @exclude
 */
public interface AsyncCallback<T> {
    void onSuccess(T result);

    void onError(Exception exception);
}
