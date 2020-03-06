package net.gini.android.vision.review

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.gini.android.vision.Document
import net.gini.android.vision.GiniVision
import net.gini.android.vision.document.ImageDocument
import net.gini.android.vision.internal.camera.photo.Photo
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
class ReviewFragmentImplTest {

    @After
    fun after() {
        GiniVision.cleanup(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun `triggers Next event`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        val document = mock<ImageDocument>()
        whenever(document.isReviewable).thenReturn(true)
        whenever(document.type).thenReturn(Document.Type.IMAGE)

        val fragmentImpl = ReviewFragmentImpl(mock(), document)
        fragmentImpl.mPhoto = mock<Photo>().apply {
            whenever(imageFormat).thenReturn(ImageDocument.ImageFormat.JPEG)
        }

        // When
        fragmentImpl.onNextClicked()

        // Then
        verify(eventTracker).onReviewScreenEvent(Event(ReviewScreenEvent.NEXT))
    }
}