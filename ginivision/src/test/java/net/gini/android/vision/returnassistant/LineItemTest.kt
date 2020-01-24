package net.gini.android.vision.returnassistant

import android.os.Bundle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal
import java.util.*

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(AndroidJUnit4::class)
class LineItemTest {

    @Test
    fun `should be parcelable`() {
        // Given
        val orig = LineItem("id", "description", 3, "12.13:EUR")

        // When
        val fromParcel: LineItem? = Bundle().apply {
            putParcelable("parcelable", orig)
        }.getParcelable("parcelable")

        // Then
        assertThat(fromParcel).isEqualTo(orig)
    }

    @Test
    fun `should parse raw amount with dot decimal separator`() {
        // Given
        val lineItem = LineItem("id", "description", 3, "12.13:EUR")

        // Then
        assertThat(lineItem.amount).isEqualTo(BigDecimal("12.13"))
        assertThat(lineItem.totalAmount).isEqualTo(BigDecimal("36.39"))
        assertThat(lineItem.currency).isEqualTo(Currency.getInstance("EUR"))
        assertThat(lineItem.rawCurrency).isEqualTo("EUR")
    }

    @Test
    fun `should parse raw amount with comma decimal separator`() {
        // Given
        val lineItem = LineItem("id", "description", 3, "12,13:EUR")

        // Then
        assertThat(lineItem.amount).isEqualTo(BigDecimal("12.13"))
        assertThat(lineItem.totalAmount).isEqualTo(BigDecimal("36.39"))
        assertThat(lineItem.currency).isEqualTo(Currency.getInstance("EUR"))
        assertThat(lineItem.rawCurrency).isEqualTo("EUR")
    }

    @Test
    fun `should create raw amount in english format`() {
        // Given
        val rawAmount = LineItem.createRawAmount(BigDecimal("12.13"), "EUR")

        // Then
        assertThat(rawAmount).isEqualTo("12.13:EUR")
    }

    @Test
    fun `should default amount to 0 when the raw amount format is not supported`() {
        val lineItem = LineItem("id", "description", 3, "100,200.13:EUR")

        // Then
        assertThat(lineItem.amount).isEqualTo(BigDecimal("0"))
        assertThat(lineItem.totalAmount).isEqualTo(BigDecimal("0"))
        assertThat(lineItem.currency).isEqualTo(Currency.getInstance("EUR"))
        assertThat(lineItem.rawCurrency).isEqualTo("EUR")
    }
}