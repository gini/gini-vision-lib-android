package net.gini.android.vision.digitalinvoice

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(AndroidJUnit4::class)
class SelectableLineItemTest {

    @Test
    fun `should be parcelable`() {
        // Given
        val orig = SelectableLineItem(true, null, LineItem("id", "description", 3, "12.13:EUR"))

        // When
        val fromParcel: SelectableLineItem? = Bundle().apply {
            putParcelable("parcelable", orig)
        }.getParcelable("parcelable")

        // Then
        assertThat(fromParcel).isEqualTo(orig)
    }
}