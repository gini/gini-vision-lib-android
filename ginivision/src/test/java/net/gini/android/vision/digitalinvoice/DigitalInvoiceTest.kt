package net.gini.android.vision.digitalinvoice

import com.google.common.truth.Truth.assertThat
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction
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
class DigitalInvoiceTest {

    private fun createLineItemsFixture() = mutableMapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
            mutableListOf(
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Shoe", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList()),
                            "baseGross" to GiniVisionSpecificExtraction("baseGross", "9.99:EUR", "", null, emptyList()),
                            "articleNumber" to GiniVisionSpecificExtraction("articleNumber", "8947278", "", null, emptyList())
                    ),
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Trouser", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList()),
                            "baseGross" to GiniVisionSpecificExtraction("baseGross", "24.39:EUR", "", null, emptyList()),
                            "articleNumber" to GiniVisionSpecificExtraction("articleNumber", "1232411", "", null, emptyList())
                    ),
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Socks", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList()),
                            "baseGross" to GiniVisionSpecificExtraction("baseGross", "4.19:EUR", "", null, emptyList()),
                            "articleNumber" to GiniVisionSpecificExtraction("articleNumber", "55789642", "", null, emptyList())
                    )
            )
    ))


    @Test
    fun `should select all line items by default`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())

        // Then
        digitalInvoice.selectableLineItems.forEach { sli ->
            assertThat(sli.selected).isTrue()
        }
    }

    @Test
    fun `should calculate selected line items sum`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())

        // When
        val sum = digitalInvoice.selectedLineItemsTotalGrossPriceSum()

        // Then
        assertThat(sum).isEqualTo(BigDecimal("48.56"))
    }

    @Test
    fun `line items sum should be zero, if there are no line items`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), emptyMap())

        // When
        val sum = digitalInvoice.selectedLineItemsTotalGrossPriceSum()

        // Then
        assertThat(sum).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `line items sum should be zero, if there are no selected line items`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())
        digitalInvoice.selectableLineItems.forEach { sli -> sli.selected = false }

        // When
        val sum = digitalInvoice.selectedLineItemsTotalGrossPriceSum()

        // Then
        assertThat(sum).isEqualTo(BigDecimal.ZERO)
    }

    @Test
    fun `should get currency for all line items from first line item`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())

        // When
        val currency = digitalInvoice.lineItemsCurency()

        // Then
        assertThat(currency).isEqualTo(Currency.getInstance("EUR"))
    }

    @Test
    fun `currency should be null if there are no line items`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), emptyMap())

        // When
        val currency = digitalInvoice.lineItemsCurency()

        // Then
        assertThat(currency).isNull()
    }

    @Test
    fun `should get gross price integral part with currency`() {
        // Given
        val grossPrice = BigDecimal("1.99")
        val currency = Currency.getInstance("EUR")

        // When
        val integralPartWithCurrency = DigitalInvoice.priceIntegralPartWithCurrencySymbol(grossPrice, currency)

        // Then
        assertThat(integralPartWithCurrency).isEqualTo("${Currency.getInstance("EUR").symbol}1")
    }

    @Test
    fun `should get gross price integral part without currency, if currency is null`() {
        // Given
        // Given
        val grossPrice = BigDecimal("1.99")

        // When
        val integralPartWithCurrency = DigitalInvoice.priceIntegralPartWithCurrencySymbol(grossPrice, null)

        // Then
        assertThat(integralPartWithCurrency).isEqualTo("1")
    }

    @Test
    fun `should get line item total gross price integral and fractional parts with currency`() {
        // Given
        val lineItem = LineItem("id", "a line item", 2, "2.49:EUR")

        // When
        val (integral, fractional) = DigitalInvoice.lineItemTotalGrossPriceIntegralAndFractionalParts(lineItem)

        // Then
        assertThat(integral).isEqualTo("${Currency.getInstance("EUR").symbol}4")
        assertThat(fractional).isEqualTo("${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}98")
    }

    @Test
    fun `should get line items sum integral and fractional parts`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())

        // When
        val (integral, fractional) = digitalInvoice.totalPriceIntegralAndFractionalParts()

        // Then
        assertThat(integral).isEqualTo("${Currency.getInstance("EUR").symbol}48")
        assertThat(fractional).isEqualTo("${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}56")
    }

    @Test
    fun `should parse the line items from the compound extractions`() {
        // Given
        val lineItemsFixture = createLineItemsFixture()

        // When
        val digitalInvoice = DigitalInvoice(emptyMap(), lineItemsFixture)

        // Then
        val lineItems = digitalInvoice.selectableLineItems.map { it.lineItem }

        assertThat(lineItems).hasSize(3)

        lineItemsFixture["lineItems"]!!.specificExtractionMaps.forEachIndexed { index, map ->
            lineItems[index].run {
                assertThat(map["description"]!!.value).isEqualTo(description)
                assertThat(map["quantity"]!!.value).isEqualTo(quantity.toString())
                assertThat(map["baseGross"]!!.value).isEqualTo(rawGrossPrice)
            }
        }
    }

    @Test
    fun `should update line item`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())

        // When
        digitalInvoice.updateLineItem(digitalInvoice.selectableLineItems[0].run {
            copy(lineItem = lineItem.copy(description = "This line item was modified"))
        })

        // Then
        assertThat(digitalInvoice.selectableLineItems[0].lineItem.description).isEqualTo("This line item was modified")
    }

    @Test
    fun `should select line item and reset the reason`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())
        digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], "I don't want that")

        // When
        digitalInvoice.selectLineItem(digitalInvoice.selectableLineItems[0])

        // Then
        assertThat(digitalInvoice.selectableLineItems[0].selected).isTrue()
        assertThat(digitalInvoice.selectableLineItems[0].reason).isNull()
    }

    @Test
    fun `should deselect line item and add reason`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())
        digitalInvoice.selectLineItem(digitalInvoice.selectableLineItems[0])

        // When
        digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], "I don't want that")

        // Then
        assertThat(digitalInvoice.selectableLineItems[0].selected).isFalse()
        assertThat(digitalInvoice.selectableLineItems[0].reason).isEqualTo("I don't want that")
    }

    @Test
    fun `should return selected and total line items count`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())
        digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], "Nem kell")

        // When
        val (selected, total) = digitalInvoice.selectedAndTotalLineItemsCount()

        // Then
        assertThat(selected).isEqualTo(2)
        assertThat(total).isEqualTo(4)
    }

    @Test
    fun `should update amountToPay extraction with the total price of selected line items`() {
        // Given
        val extractions = mapOf("amountToPay" to GiniVisionSpecificExtraction("amountToPay", "1.99:EUR", "amount", null, emptyList()))

        val digitalInvoice = DigitalInvoice(extractions, createLineItemsFixture())
        digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], "Nem kell")

        // When
        digitalInvoice.updateAmountToPayExtractionWithTotalPrice()

        // Then
        assertThat(digitalInvoice.extractions["amountToPay"]!!.value).isEqualTo("28.58:EUR")
        assertThat(digitalInvoice.extractions["amountToPay"]!!.name).isEqualTo(extractions["amountToPay"]!!.name)
        assertThat(digitalInvoice.extractions["amountToPay"]!!.entity).isEqualTo(extractions["amountToPay"]!!.entity)
        assertThat(digitalInvoice.extractions["amountToPay"]!!.candidates).isEqualTo(extractions["amountToPay"]!!.candidates)
        assertThat(digitalInvoice.extractions["amountToPay"]!!.box).isEqualTo(extractions["amountToPay"]!!.box)
        assertThat(digitalInvoice.extractions["amountToPay"]!!.isDirty).isEqualTo(extractions["amountToPay"]!!.isDirty)
    }

    @Test
    fun `should add amountToPay to extractions with the total price of selected line items`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())
        digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[1], "Nem kell")

        // When
        digitalInvoice.updateAmountToPayExtractionWithTotalPrice()

        // Then
        assertThat(digitalInvoice.extractions["amountToPay"]!!.value).isEqualTo("24.17:EUR")
    }

    @Test
    fun `should set quantity to 0 in the 'lineItems' compound extractions for deselected line items`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())

        digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], "I don't want this")

        // When
        digitalInvoice.updateLineItemExtractionsWithReviewedLineItems()

        // Then
        assertThat(digitalInvoice.compoundExtractions["lineItems"]!!.specificExtractionMaps[0]["quantity"]!!.value).isEqualTo("0")
    }

    @Test
    fun `should update the 'lineItems' compound extractions with the line item changes`() {
        // Given
        val digitalInvoice = DigitalInvoice(emptyMap(), createLineItemsFixture())

        digitalInvoice.updateLineItem(digitalInvoice.selectableLineItems[1]
                .copy(lineItem = digitalInvoice.selectableLineItems[1].lineItem
                        .copy(description = "This was modified", quantity = 99)))
        digitalInvoice.updateLineItem(digitalInvoice.selectableLineItems[2]
                .copy(lineItem = digitalInvoice.selectableLineItems[2].lineItem
                        .copy(rawGrossPrice = "79.19:EUR")))

        // When
        digitalInvoice.updateLineItemExtractionsWithReviewedLineItems()

        // Then
        val lineItemExtractions = digitalInvoice.compoundExtractions["lineItems"]!!.specificExtractionMaps
        assertThat(lineItemExtractions[1]["description"]!!.value).isEqualTo("This was modified")
        assertThat(lineItemExtractions[1]["quantity"]!!.value).isEqualTo("99")
        assertThat(lineItemExtractions[2]["baseGross"]!!.value).isEqualTo("79.19:EUR")
    }
}