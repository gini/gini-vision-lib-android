package net.gini.android.vision.tracking;

import net.gini.android.vision.GiniVision;

import java.util.Collections;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public final class EventTrackingHelper {

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackOnboardingScreenEvent(@NonNull final OnboardingScreenEvent event, @NonNull final Map<String, Object> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getEventTracker().onOnboardingScreenEvent(new Event<>(event, details));
        }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackOnboardingScreenEvent(@NonNull final OnboardingScreenEvent event) {
        trackOnboardingScreenEvent(event, Collections.<String, Object>emptyMap());
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackCameraScreenEvent(@NonNull final CameraScreenEvent event, @NonNull final Map<String, Object> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getEventTracker().onCameraScreenEvent(new Event<>(event, details));
        }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackCameraScreenEvent(@NonNull final CameraScreenEvent event) {
        trackCameraScreenEvent(event, Collections.<String, Object>emptyMap());
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackReviewScreenEvent(@NonNull final ReviewScreenEvent event, @NonNull final Map<String, Object> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getEventTracker().onReviewScreenEvent(new Event<>(event, details));
        }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackReviewScreenEvent(@NonNull final ReviewScreenEvent event) {
        trackReviewScreenEvent(event, Collections.<String, Object>emptyMap());
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackAnalysisScreenEvent(@NonNull final AnalysisScreenEvent event, @NonNull final Map<String, Object> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().internal().getEventTracker().onAnalysisScreenEvent(new Event<>(event, details));
        }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    public static void trackAnalysisScreenEvent(@NonNull final AnalysisScreenEvent event) {
        trackAnalysisScreenEvent(event, Collections.<String, Object>emptyMap());
    }

    private EventTrackingHelper() {
    }
}
