package net.gini.android.vision.digitalinvoice

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.math.BigDecimal
import java.util.*

/**
 * Created by Alpar Szotyori on 12.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(JUnit4::class)
class LineItemsGrossPriceHelperTest {

    @Test
    fun `should calculate selected line items sum`() {
        // Given
        val lineItems = listOf(
                SelectableLineItem(selected = false,
                        lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 1,
                                rawGrossPrice = "1.19:EUR")),
                SelectableLineItem(selected = true,
                        lineItem = LineItem(id = "2", description = "Line Item 2", quantity = 2,
                                rawGrossPrice = "5.89:EUR")),
                SelectableLineItem(selected = true,
                        lineItem = LineItem(id = "3", description = "Line Item 3", quantity = 1,
                                rawGrossPrice = "9.99:EUR"))
        )

        // When
        val sum = selectedLineItemsTotalGrossPriceSum(lineItems)

        // Then
        assertThat(sum).isEqualTo(BigDecimal("21.77"))
    }

    @Test
    fun `line items sum should be zero, if there are no line items`() {
        // Given
        val lineItems: List<SelectableLineItem> = emptyList()

        // When
        val sum = selectedLineItemsTotalGrossPriceSum(lineItems)

        // Then
        assertThat(sum).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `line items sum should be zero, if there are no selected line items`() {
        // Given
        val lineItems = listOf(
                SelectableLineItem(selected = false,
                        lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 1,
                                rawGrossPrice = "1.19:EUR")),
                SelectableLineItem(selected = false,
                        lineItem = LineItem(id = "2", description = "Line Item 2", quantity = 1,
                                rawGrossPrice = "5.89:EUR")),
                SelectableLineItem(selected = false,
                        lineItem = LineItem(id = "3", description = "Line Item 3", quantity = 1,
                                rawGrossPrice = "9.99:EUR"))
        )

        // When
        val sum = selectedLineItemsTotalGrossPriceSum(lineItems)

        // Then
        assertThat(sum).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should get currency for all line items from first line item`() {
        // Given
        val lineItems = listOf(
                SelectableLineItem(selected = false,
                        lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 1,
                                rawGrossPrice = "1.19:EUR")),
                SelectableLineItem(selected = false,
                        lineItem = LineItem(id = "2", description = "Line Item 2", quantity = 1,
                                rawGrossPrice = "5.89:EUR")),
                SelectableLineItem(selected = false,
                        lineItem = LineItem(id = "3", description = "Line Item 3", quantity = 1,
                                rawGrossPrice = "9.99:EUR"))
        )

        // When
        val currency = lineItemsCurency(lineItems)

        // Then
        assertThat(currency).isEqualTo(Currency.getInstance("EUR"))
    }

    @Test
    fun `currency should be null if there are no line items`() {
        // Given
        val lineItems: List<SelectableLineItem> = emptyList()

        // When
        val currency = lineItemsCurency(lineItems)

        // Then
        assertThat(currency).isNull()
    }

    @Test
    fun `should get gross price integral part with currency`() {
        // Given
        val grossPrice = BigDecimal("1.99")
        val currency = Currency.getInstance("EUR")

        // When
        val integralPartWithCurrency = grossPriceIntegralPartWithCurrencySymbol(grossPrice, currency)

        // Then
        assertThat(integralPartWithCurrency).isEqualTo("${Currency.getInstance("EUR").symbol}1")
    }

    @Test
    fun `should get gross price integral part without currency, if currency is null`() {
        // Given
        val grossPrice = BigDecimal("1.99")

        // When
        val integralPartWithCurrency = grossPriceIntegralPartWithCurrencySymbol(grossPrice, null)

        // Then
        assertThat(integralPartWithCurrency).isEqualTo("1")
    }

    @Test
    fun `should get line items sum integral and fractional parts`() {
        // Given
        val lineItems = listOf(
                SelectableLineItem(selected = true,
                        lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 1,
                                rawGrossPrice = "1.19:EUR")),
                SelectableLineItem(selected = true,
                        lineItem = LineItem(id = "2", description = "Line Item 2", quantity = 1,
                                rawGrossPrice = "5.89:EUR"))
        )

        // When
        val (integral, fractional) = lineItemsTotalGrossPriceSumIntegralAndFractionalParts(lineItems)

        // Then
        assertThat(integral).isEqualTo("${Currency.getInstance("EUR").symbol}7")
        assertThat(fractional).isEqualTo("${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}08")
    }
}