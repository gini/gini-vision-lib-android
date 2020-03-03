package net.gini.android.vision.tracking;

/**
 * Created by Alpar Szotyori on 03.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */
public interface EventTracker {

    void onOnboardingScreenEvent(Event<OnboardingScreenEvent> event);

    void onCameraScreenEvent(Event<CameraScreenEvent> event);

    void onReviewScreenEvent(Event<ReviewScreenEvent> event);

    void onAnalysisScreenEvent(Event<AnalysisScreenEvent> event);
}
