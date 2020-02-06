package net.gini.android.vision.digitalinvoice

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Alpar Szotyori on 12.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(JUnit4::class)
class BigDecimalExtensionsTest {

    @Test
    fun `should return formatted integral part`() {
        // Given
        val number = BigDecimal("28.92")
        val format = DecimalFormat("000")

        // When
        val result = number.integralPart(format)

        // Then
        assertThat(result).isEqualTo("028")
    }

    @Test
    fun `should return formatted fractional part`() {
        // Given
        val number = BigDecimal("28.92")
        val format = DecimalFormat(".000")

        // When
        val result = number.fractionalPart(format)

        // Then
        assertThat(result).isEqualTo(".920")
    }

    @Test
    fun `should return formatted integral part with currency`() {
        // Given
        val number = BigDecimal("28.92")
        val format = DecimalFormat("000")
        val currency = Currency.getInstance("EUR")

        // When
        val result = number.integralPartWithCurrency(currency, format)

        // Then
        assertThat(result).isEqualTo("${currency.symbol}028")
    }
}