package net.gini.android.vision.tracking;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

import android.app.Activity;

import net.gini.android.vision.GiniVision;

/**
 * Implement this interface and pass it to the {@link GiniVision.Builder#setEventTracker(EventTracker)} to get informed about various events
 * occuring during the usage of the Gini Vision Library.
 *
 * <p> Check each event enum to find out which events are triggered on the different screens.
 *
 * <p> If you use the Screen API all events will be triggered automatically.
 *
 * <p> If you use the Component API some events will not be triggered (for ex. events which rely on {@link Activity#onBackPressed()}). You
 * need to check whether all the events you are interested in are triggered.
 */
public interface EventTracker {

    /**
     * Called when an event is triggered on the onboarding screen.
     *
     * <p> See {@link OnboardingScreenEvent} for possible events.
     *
     * @param event the onboarding screen event
     */
    void onOnboardingScreenEvent(Event<OnboardingScreenEvent> event);

    /**
     * Called when an event is triggered on the camera screen.
     *
     * <p> See {@link CameraScreenEvent} for possible events.
     *
     * @param event the camera screen event
     */
    void onCameraScreenEvent(Event<CameraScreenEvent> event);

    /**
     * Called when an event is triggered on the review screen.
     *
     * <p> See {@link ReviewScreenEvent} for possible events.
     *
     * @param event the review screen event
     */
    void onReviewScreenEvent(Event<ReviewScreenEvent> event);

    /**
     * Called when an event is triggered on the analysis screen.
     *
     * <p> See {@link AnalysisScreenEvent} for possible events.
     *
     * @param event the analysis screen event
     */
    void onAnalysisScreenEvent(Event<AnalysisScreenEvent> event);
}
