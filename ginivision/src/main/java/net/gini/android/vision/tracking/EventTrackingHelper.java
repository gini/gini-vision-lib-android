package net.gini.android.vision.tracking;

import android.support.annotation.NonNull;

import net.gini.android.vision.GiniVision;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */
public final class EventTrackingHelper {

    public static void trackOnboardingScreenEvent(@NonNull final OnboardingScreenEvent event, @NonNull final Map<String, String> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().getEventTracker().onOnboardingScreenEvent(new Event<>(event, details));
        }
    }

    public static void trackOnboardingScreenEvent(@NonNull final OnboardingScreenEvent event) {
        trackOnboardingScreenEvent(event, Collections.<String, String>emptyMap());
    }

    public static void trackCameraScreenEvent(@NonNull final CameraScreenEvent event, @NonNull final Map<String, String> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().getEventTracker().onCameraScreenEvent(new Event<>(event, details));
        }
    }

    public static void trackCameraScreenEvent(@NonNull final CameraScreenEvent event) {
        trackCameraScreenEvent(event, Collections.<String, String>emptyMap());
    }

    public static void trackReviewScreenEvent(@NonNull final ReviewScreenEvent event, @NonNull final Map<String, String> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().getEventTracker().onReviewScreenEvent(new Event<>(event, details));
        }
    }

    public static void trackReviewScreenEvent(@NonNull final ReviewScreenEvent event) {
        trackReviewScreenEvent(event, Collections.<String, String>emptyMap());
    }

    public static void trackAnalysisScreenEvent(@NonNull final AnalysisScreenEvent event, @NonNull final Map<String, String> details) {
        if (GiniVision.hasInstance()) {
            GiniVision.getInstance().getEventTracker().onAnalysisScreenEvent(new Event<>(event, details));
        }
    }

    public static void trackAnalysisScreenEvent(@NonNull final AnalysisScreenEvent event) {
        trackAnalysisScreenEvent(event, Collections.<String, String>emptyMap());
    }

    private EventTrackingHelper() {
    }
}
