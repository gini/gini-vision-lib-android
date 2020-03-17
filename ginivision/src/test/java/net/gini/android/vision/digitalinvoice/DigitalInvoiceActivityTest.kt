package net.gini.android.vision.digitalinvoice

import android.app.Activity
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import net.gini.android.vision.camera.CameraActivity
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode
import org.robolectric.annotation.LooperMode.Mode.PAUSED

/**
 * Created by Alpar Szotyori on 24.02.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

@RunWith(AndroidJUnit4::class)
class DigitalInvoiceActivityTest {

    @LooperMode(PAUSED)
    @Test
    fun `return extractions when 'Pay' button was clicked`() {
        // Given
        ActivityScenario.launch(DigitalInvoiceActivity::class.java).use { scenario ->
            scenario.moveToState(Lifecycle.State.STARTED)

            // When
            scenario.onActivity { activity ->
                activity.onPayInvoice(specificExtractions = mapOf("amountToPay" to mock()),
                        compoundExtractions = mapOf("lineItems" to mock()))
            }

            // Then
            assertThat(scenario.result.resultCode).isEqualTo(Activity.RESULT_OK)

            val extractionsBundle = scenario.result.resultData.getBundleExtra(CameraActivity.EXTRA_OUT_EXTRACTIONS)
            assertThat(extractionsBundle).isNotNull()

            val extraction = extractionsBundle!!.getParcelable<GiniVisionSpecificExtraction>("amountToPay")
            assertThat(extraction).isNotNull()

            val compoundExtractionsBundle = scenario.result.resultData.getBundleExtra(CameraActivity.EXTRA_OUT_COMPOUND_EXTRACTIONS)
            assertThat(compoundExtractionsBundle).isNotNull()

            val compoundExtraction = compoundExtractionsBundle!!.getParcelable<GiniVisionCompoundExtraction>("lineItems")
            assertThat(compoundExtraction).isNotNull()
        }
    }

}