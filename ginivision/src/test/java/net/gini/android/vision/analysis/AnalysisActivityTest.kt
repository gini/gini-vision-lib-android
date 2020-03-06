package net.gini.android.vision.analysis

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import net.gini.android.vision.Document
import net.gini.android.vision.GiniVision
import net.gini.android.vision.document.ImageDocument
import net.gini.android.vision.tracking.AnalysisScreenEvent
import net.gini.android.vision.tracking.Event
import net.gini.android.vision.tracking.EventTracker
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Alpar Szotyori on 02.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

@RunWith(AndroidJUnit4::class)
//@LooperMode(LooperMode.Mode.PAUSED)
class AnalysisActivityTest {

    @After
    fun after() {
        GiniVision.cleanup(getInstrumentation().targetContext)
    }

    @Test
    fun `triggers Cancel event when back was pressed`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()
        GiniVision.getInstance().internal().imageMultiPageDocumentMemoryStore.setMultiPageDocument(mock())

        ActivityScenario.launch<AnalysisActivity>(Intent(getInstrumentation().targetContext, AnalysisActivity::class.java).apply {
            putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, mock<ImageDocument>().apply {
                whenever(isReviewable).thenReturn(true)
                whenever(type).thenReturn(Document.Type.IMAGE)
            })
        }).use { scenario ->
            scenario.moveToState(Lifecycle.State.STARTED)

            // When
            scenario.onActivity {activity ->
                activity.onBackPressed()

                // Then
                verify(eventTracker).onAnalysisScreenEvent(Event(AnalysisScreenEvent.CANCEL))
            }
        }
    }
}
