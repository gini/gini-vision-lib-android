package net.gini.android.vision.onboarding;

/**
 * Created by Alpar Szotyori on 21.02.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

import androidx.annotation.NonNull;

/**
 * <p>
 *     Methods which both Onboarding Fragments must implement.
 * </p>
 */
public interface OnboardingFragmentInterface {

    /**
     * <p>
     *     Set a listener for onboarding events.
     * </p>
     * <p>
     *     By default the hosting Activity is expected to implement
     *     the {@link OnboardingFragmentListener}. In case that is not feasible you may set the
     *     listener using this method.
     * </p>
     * <p>
     *     <b>Note:</b> the listener is expected to be available until the fragment is
     *     attached to an activity. Make sure to set the listener before that.
     * </p>
     * @param listener {@link OnboardingFragmentListener} instance
     */
    void setListener(@NonNull final OnboardingFragmentListener listener);

}
