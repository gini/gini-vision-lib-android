package net.gini.android.vision.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

public class ViewStubSafeInflater {

    private final ViewStub mViewStub;
    private boolean mInflated = false;

    public ViewStubSafeInflater(@NonNull ViewStub viewStub) {
        mViewStub = viewStub;
    }

    public boolean isInflated() {
        return mInflated;
    }

    @Nullable
    public View inflate() {
        if (mInflated) {
            return null;
        }
        mInflated = true;
        return mViewStub.inflate();
    }
}
