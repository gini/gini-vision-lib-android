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

    private fun createLineItemsFixture() = mutableMapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
            mutableListOf(
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Shoe", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList()),
                            "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "9.99:EUR", "", null, emptyList()),
                            "artNumber" to GiniVisionSpecificExtraction("artNumber", "8947278", "", null, emptyList())
                    ),
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Trouser", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList()),
                            "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "24.39:EUR", "", null, emptyList()),
                            "artNumber" to GiniVisionSpecificExtraction("artNumber", "1232411", "", null, emptyList())
                    )
            )
    ))

    @Test
    fun `line items available`() {
        // When
        var valid = true
        try {
            LineItemsValidator.validate(createLineItemsFixture())
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
            LineItemsValidator.validate(mapOf("somethingElse" to mock()))
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
            LineItemsValidator.validate(createLineItemsFixture())
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
            LineItemsValidator.validate(createLineItemsFixture().apply {
                get("lineItems")!!.specificExtractionMaps[0].remove("description")
            })
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
            LineItemsValidator.validate(createLineItemsFixture())
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
            LineItemsValidator.validate(createLineItemsFixture().apply {
                get("lineItems")!!.specificExtractionMaps[0].remove("quantity")
            })
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
            LineItemsValidator.validate(createLineItemsFixture())
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
            LineItemsValidator.validate(createLineItemsFixture().apply {
                get("lineItems")!!.specificExtractionMaps[0].remove("grossPrice")
            })
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
            LineItemsValidator.validate(createLineItemsFixture())
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
            LineItemsValidator.validate(createLineItemsFixture().apply {
                get("lineItems")!!.specificExtractionMaps[0].remove("artNumber")
            })
        } catch (e: ArticleNumberMissingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `quantity parcelable`() {
        // When
        var valid = true
        try {
            LineItemsValidator.validate(createLineItemsFixture())
        } catch (e: QuantityParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `quantity not parcelable`() {
        // When
        var valid = true
        try {
            LineItemsValidator.validate(createLineItemsFixture().apply {
                get("lineItems")!!.specificExtractionMaps[0]["quantity"]!!.value = "NaN"
            })
        } catch (e: QuantityParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `gross price parcelable`() {
        // When
        var valid = true
        try {
            LineItemsValidator.validate(createLineItemsFixture())
        } catch (e: GrossPriceParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `gross price not parcelable`() {
        // When
        var valid = true
        try {
            LineItemsValidator.validate(createLineItemsFixture().apply {
                get("lineItems")!!.specificExtractionMaps[0]["grossPrice"]!!.value = "9_89:EUR"
            })
        } catch (e: GrossPriceParsingException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }

    @Test
    fun `single currency`() {
        // When
        var valid = true
        try {
            LineItemsValidator.validate(createLineItemsFixture())
        } catch (e: MixedCurrenciesException) {
            valid = false
        }

        // Then
        assertThat(valid).isTrue()
    }

    @Test
    fun `mixed currencies`() {
        // When
        var valid = true
        try {
            LineItemsValidator.validate(createLineItemsFixture().apply {
                get("lineItems")!!.specificExtractionMaps[0]["grossPrice"]!!.value = "9.89:USD"
            })
        } catch (e: MixedCurrenciesException) {
            valid = false
        }

        // Then
        assertThat(valid).isFalse()
    }
}