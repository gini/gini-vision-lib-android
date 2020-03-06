package net.gini.android.vision.review.multipage

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
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
import org.robolectric.annotation.LooperMode

/**
 * Created by Alpar Szotyori on 02.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MultipageReviewActivityTest {

    @After
    fun after() {
        GiniVision.cleanup(getInstrumentation().targetContext)
    }

    @Test
    fun `triggers Back event when back was pressed`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()
        GiniVision.getInstance().internal().imageMultiPageDocumentMemoryStore.setMultiPageDocument(mock())

        ActivityScenario.launch<MultiPageReviewActivity>(Intent(getInstrumentation().targetContext, MultiPageReviewActivity::class.java)).use { scenario ->
            scenario.moveToState(Lifecycle.State.STARTED)

            // When
            scenario.onActivity {activity ->
                activity.onBackPressed()

                // Then
                verify(eventTracker).onReviewScreenEvent(Event(ReviewScreenEvent.BACK))
            }
        }
    }
}
