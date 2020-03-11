package net.gini.android.vision.digitalinvoice

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import net.gini.android.vision.digitalinvoice.DigitalInvoiceException.*
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * Created by Alpar Szotyori on 11.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */
@RunWith(JUnit4::class)
class LineItemsValidatorTest {

    @Test
    fun `line items available`() {
        // When
        var valid = true
        try {
            lineItemsAvailable(mapOf("lineItems" to mock()))
        } catch (e: LineItemsMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `line items not available`() {
        // When
        var valid = true
        try {
            lineItemsAvailable(mapOf("somethingElse" to mock()))
        } catch (e: LineItemsMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `description available`() {
        // When
        var valid = true
        try {
            descriptionAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("description" to mock())))
                    )
            )
        } catch (e: DescriptionMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `description not available`() {
        // When
        var valid = true
        try {
            descriptionAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(emptyMap(), mapOf("description" to mock())))
                    )
            )
        } catch (e: DescriptionMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `quantity available`() {
        // When
        var valid = true
        try {
            quantityAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("quantity" to mock())))
                    )
            )
        } catch (e: QuantityMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `quantity not available`() {
        // When
        var valid = true
        try {
            quantityAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(emptyMap(), mapOf("quantity" to mock())))
                    )
            )
        } catch (e: QuantityMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `gross price available`() {
        // When
        var valid = true
        try {
            grossPriceAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("grossPrice" to mock())))
                    )
            )
        } catch (e: GrossPriceMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `gross price not available`() {
        // When
        var valid = true
        try {
            grossPriceAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(emptyMap(), mapOf("grossPrice" to mock())))
                    )
            )
        } catch (e: GrossPriceMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `article number available`() {
        // When
        var valid = true
        try {
            articleNumberAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("articleNumber" to mock())))
                    )
            )
        } catch (e: ArticleNumberMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `article number not available`() {
        // When
        var valid = true
        try {
            articleNumberAvailable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(emptyMap(), mapOf("articleNumber" to mock())))
                    )
            )
        } catch (e: ArticleNumberMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `quantity parcelable`() {
        // Given
        val quantity = GiniVisionSpecificExtraction("quantity", "3","",null, emptyList())

        // When
        var valid = true
        try {
            quantityParcelable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("quantity" to quantity)))
                    )
            )
        } catch (e: QuantityParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `quantity not parcelable`() {
        // Given
        val quantity = GiniVisionSpecificExtraction("quantity", "NaN","",null, emptyList())

        // When
        var valid = true
        try {
            quantityParcelable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(emptyMap(), mapOf("quantity" to quantity)))
                    )
            )
        } catch (e: QuantityParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `gross price parcelable`() {
        // Given
        val grossPrice = GiniVisionSpecificExtraction("grossPrice", "9.89:EUR","",null, emptyList())

        // When
        var valid = true
        try {
            grossPriceParcelable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("grossPrice" to grossPrice)))
                    )
            )
        } catch (e: GrossPriceParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `gross price not parcelable`() {
        // Given
        val grossPrice = GiniVisionSpecificExtraction("grossPrice", "9_89:EUR","",null, emptyList())

        // When
        var valid = true
        try {
            grossPriceParcelable(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("grossPrice" to grossPrice)))
                    )
            )
        } catch (e: GrossPriceParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `single currency`() {
        // Given
        val grossPrice1 = GiniVisionSpecificExtraction("grossPrice", "9.89:EUR","",null, emptyList())
        val grossPrice2 = GiniVisionSpecificExtraction("grossPrice", "3.49:EUR","",null, emptyList())
        val grossPrice3 = GiniVisionSpecificExtraction("grossPrice", "1.99:EUR","",null, emptyList())

        // When
        var valid = true
        try {
            singleCurrency(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("grossPrice" to grossPrice1),
                                    mapOf("grossPrice" to grossPrice2),
                                    mapOf("grossPrice" to grossPrice3)))
                    )
            )
        } catch (e: MixedCurrenciesException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `mixed currencies`() {
        // Given
        val grossPrice1 = GiniVisionSpecificExtraction("grossPrice", "9.89:EUR","",null, emptyList())
        val grossPrice2 = GiniVisionSpecificExtraction("grossPrice", "3.49:USD","",null, emptyList())
        val grossPrice3 = GiniVisionSpecificExtraction("grossPrice", "1.99:EUR","",null, emptyList())

        // When
        var valid = true
        try {
            singleCurrency(
                    mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                            listOf(mapOf("grossPrice" to grossPrice1),
                                    mapOf("grossPrice" to grossPrice2),
                                    mapOf("grossPrice" to grossPrice3)))
                    )
            )
        } catch (e: MixedCurrenciesException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }
}