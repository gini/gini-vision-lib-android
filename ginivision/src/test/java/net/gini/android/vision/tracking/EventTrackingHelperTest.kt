package net.gini.android.vision.tracking

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nhaarman.mockitokotlin2.*
import net.gini.android.vision.GiniVision
import net.gini.android.vision.GiniVisionHelper
import net.gini.android.vision.tracking.EventTrackingHelper.*
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Alpar Szotyori on 02.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

@RunWith(AndroidJUnit4::class)
class EventTrackingHelperTest {

    @After
    fun after() {
        GiniVisionHelper.setGiniVisionInstance(null)
    }

    @Test
    fun `track Onboarding Screen events when GiniVision instance is available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        trackOnboardingScreenEvent(OnboardingScreenEvent.START, mapOf("detail" to "Event detail"))

        // Then
        verify(eventTracker).onOnboardingScreenEvent(eq(Event(OnboardingScreenEvent.START, mapOf("detail" to "Event detail"))))
    }

    @Test
    fun `don't track Onboarding Screen events when GiniVision instance is not available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        GiniVisionHelper.setGiniVisionInstance(null)

        trackOnboardingScreenEvent(OnboardingScreenEvent.START)

        // Then
        verify(eventTracker, never()).onOnboardingScreenEvent(any())
    }

    @Test
    fun `track Camera Screen events when GiniVision instance is available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        trackCameraScreenEvent(CameraScreenEvent.TAKE_PICTURE, mapOf("detail" to "Event detail"))

        // Then
        verify(eventTracker).onCameraScreenEvent(eq(Event(CameraScreenEvent.TAKE_PICTURE, mapOf("detail" to "Event detail"))))
    }

    @Test
    fun `don't track Camera Screen events when GiniVision instance is not available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        GiniVisionHelper.setGiniVisionInstance(null)

        trackCameraScreenEvent(CameraScreenEvent.TAKE_PICTURE)

        // Then
        verify(eventTracker, never()).onCameraScreenEvent(any())
    }

    @Test
    fun `track Review Screen events when GiniVision instance is available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        trackReviewScreenEvent(ReviewScreenEvent.NEXT, mapOf("detail" to "Event detail"))

        // Then
        verify(eventTracker).onReviewScreenEvent(eq(Event(ReviewScreenEvent.NEXT, mapOf("detail" to "Event detail"))))
    }

    @Test
    fun `don't track Review Screen events when GiniVision instance is not available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        GiniVisionHelper.setGiniVisionInstance(null)

        trackReviewScreenEvent(ReviewScreenEvent.NEXT)

        // Then
        verify(eventTracker, never()).onReviewScreenEvent(any())
    }

    @Test
    fun `track Analysis Screen events when GiniVision instance is available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        trackAnalysisScreenEvent(AnalysisScreenEvent.RETRY, mapOf("detail" to "Event detail"))

        // Then
        verify(eventTracker).onAnalysisScreenEvent(eq(Event(AnalysisScreenEvent.RETRY, mapOf("detail" to "Event detail"))))
    }

    @Test
    fun `don't track Analysis Screen events when GiniVision instance is not available`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        // When
        GiniVisionHelper.setGiniVisionInstance(null)

        trackAnalysisScreenEvent(AnalysisScreenEvent.RETRY)

        // Then
        verify(eventTracker, never()).onAnalysisScreenEvent(any())
    }
}