package net.gini.android.vision.tracking

import net.gini.android.vision.GiniVision
import java.util.*

/**
 * Created by Alpar Szotyori on 27.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

interface EventTracker {
    fun onOnboardingScreenEvent(event: Event<OnboardingScreenEvent>)
    fun onCameraScreenEvent(event: Event<CameraScreenEvent>)
    fun onReviewScreenEvent(event: Event<ReviewScreenEvent>)
    fun onAnalysisScreenEvent(event: Event<AnalysisScreenEvent>)
}

class Event<T : Enum<T>> @JvmOverloads constructor(val type: T,
                                                   val details: Map<String, String> = emptyMap()) {

    override fun equals(other: Any?) = other is Event<*>
            && type == other.type
            && details == other.details

    override fun hashCode() = Objects.hash(type, details)

    override fun toString(): String {
        return "Event(type=$type, details=$details)"
    }
}

@JvmOverloads
internal fun trackOnboardingScreenEvent(event: OnboardingScreenEvent, details: Map<String, String> = emptyMap()) {
    if (GiniVision.hasInstance()) {
        GiniVision.getInstance().eventTracker.onOnboardingScreenEvent(Event(event, details))
    }
}

@JvmOverloads
internal fun trackCameraScreenEvent(event: CameraScreenEvent, details: Map<String, String> = emptyMap()) {
    if (GiniVision.hasInstance()) {
        GiniVision.getInstance().eventTracker.onCameraScreenEvent(Event(event, details))
    }
}

@JvmOverloads
internal fun trackReviewScreenEvent(event: ReviewScreenEvent, details: Map<String, String> = emptyMap()) {
    if (GiniVision.hasInstance()) {
        GiniVision.getInstance().eventTracker.onReviewScreenEvent(Event(event, details))
    }
}

@JvmOverloads
internal fun trackAnalysisScreenEvent(event: AnalysisScreenEvent, details: Map<String, String> = emptyMap()) {
    if (GiniVision.hasInstance()) {
        GiniVision.getInstance().eventTracker.onAnalysisScreenEvent(Event(event, details))
    }
}