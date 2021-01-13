package net.gini.android.vision.review.multipage;

/**
 * Created by Alpar Szotyori on 07.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import androidx.annotation.NonNull;

/**
 * Methods which the Multi-Page Review Fragment must implement.
 */
public interface MultiPageReviewFragmentInterface {

    /**
     * Set a listener for multi-page review events.
     *
     * <p> By default the hosting Activity is expected to implement the {@link
     * MultiPageReviewFragmentListener}. In case that is not feasible you may set the listener using
     * this method.
     *
     * <p> <b>Note:</b> the listener is expected to be available until the fragment is attached to
     * an activity. Make sure to set the listener before that.
     *
     * @param listener {@link MultiPageReviewFragmentListener} instance
     */
    void setListener(@NonNull final MultiPageReviewFragmentListener listener);
}
