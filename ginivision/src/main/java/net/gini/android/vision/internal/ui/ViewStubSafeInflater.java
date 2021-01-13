package net.gini.android.vision.internal.ui;

import android.view.View;
import android.view.ViewStub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Internal use only.
 *
 * @suppress
 */
public class ViewStubSafeInflater {

    private static final Logger LOG = LoggerFactory.getLogger(ViewStubSafeInflater.class);

    private final ViewStub mViewStub;
    private boolean mInflated;

    public ViewStubSafeInflater(@NonNull final ViewStub viewStub) {
        mViewStub = viewStub;
    }

    @Nullable
    public View inflate() {
        if (mInflated) {
            LOG.debug("Already inflated");
            return null;
        }
        mInflated = true;
        final View view = mViewStub.inflate();
        LOG.debug("Inflated {} to {}", mViewStub, view);
        return view;
    }
}
