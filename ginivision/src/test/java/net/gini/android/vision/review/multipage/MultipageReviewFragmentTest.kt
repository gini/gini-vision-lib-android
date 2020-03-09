package net.gini.android.vision.review.multipage

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import net.gini.android.vision.GiniVision
import net.gini.android.vision.tracking.Event
import net.gini.android.vision.tracking.EventTracker
import net.gini.android.vision.tracking.ReviewScreenEvent
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Alpar Szotyori on 02.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

@RunWith(AndroidJUnit4::class)
class MultipageReviewFragmentTest {

    @After
    fun after() {
        GiniVision.cleanup(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun `triggers Next event`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        val fragment = MultiPageReviewFragment()
        fragment.setListener(mock())

        // When
        fragment.onNextButtonClicked()

        // Then
        verify(eventTracker).onReviewScreenEvent(Event(ReviewScreenEvent.NEXT))
    }
}