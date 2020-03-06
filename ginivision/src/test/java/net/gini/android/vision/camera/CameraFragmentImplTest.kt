package net.gini.android.vision.camera

import android.app.Activity
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import jersey.repackaged.jsr166e.CompletableFuture
import net.gini.android.vision.GiniVision
import net.gini.android.vision.internal.camera.api.CameraInterface
import net.gini.android.vision.tracking.CameraScreenEvent
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
class CameraFragmentImplTest {

    @After
    fun after() {
        GiniVision.cleanup(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun `triggers Take Picture event`() {
        // Given
        val eventTracker = spy<EventTracker>()
        GiniVision.Builder().setEventTracker(eventTracker).build()

        val fragmentImpl = object: CameraFragmentImpl(mock()) {
            override fun createCameraController(activity: Activity?): CameraInterface {
                return mock<CameraInterface>().apply {
                    whenever(isPreviewRunning).thenReturn(true)
                    whenever(takePicture()).thenReturn(CompletableFuture.completedFuture(mock()))
                }
            }
        }
        fragmentImpl.initCameraController(mock())

        // When
        fragmentImpl.onCameraTriggerClicked()

        // Then
        verify(eventTracker).onCameraScreenEvent(Event(CameraScreenEvent.TAKE_PICTURE))
    }
}