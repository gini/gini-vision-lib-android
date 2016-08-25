package net.gini.android.vision.internal.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @exclude
 */
public class ViewStubSafeInflater {

    private static final Logger LOG = LoggerFactory.getLogger(ViewStubSafeInflater.class);

    private final ViewStub mViewStub;
    private boolean mInflated = false;

    public ViewStubSafeInflater(@NonNull ViewStub viewStub) {
        mViewStub = viewStub;
    }

    @Nullable
    public View inflate() {
        if (mInflated) {
            LOG.debug("Already inflated");
            return null;
        }
        mInflated = true;
        View view = mViewStub.inflate();
        LOG.debug("Inflated {} to {}", mViewStub, view);
        return view;
    }
}
