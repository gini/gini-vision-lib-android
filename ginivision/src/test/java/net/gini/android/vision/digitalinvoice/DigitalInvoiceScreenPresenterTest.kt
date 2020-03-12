package net.gini.android.vision.digitalinvoice

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import net.gini.android.vision.digitalinvoice.DigitalInvoiceScreenContract.View
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks
import java.util.*


/**
 * Created by Alpar Szotyori on 12.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(JUnit4::class)
class DigitalInvoiceScreenPresenterTest {

    @Mock
    private lateinit var activity: Activity
    @Mock
    private lateinit var view: View

    private fun createLineItemsFixture() = mutableMapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
            mutableListOf(
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Shoe", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList()),
                            "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "9.99:EUR", "", null, emptyList()),
                            "articleNumber" to GiniVisionSpecificExtraction("articleNumber", "8947278", "", null, emptyList())
                    ),
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Trouser", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList()),
                            "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "24.39:EUR", "", null, emptyList()),
                            "articleNumber" to GiniVisionSpecificExtraction("articleNumber", "1232411", "", null, emptyList())
                    ),
                    mutableMapOf(
                            "description" to GiniVisionSpecificExtraction("description", "Socks", "", null, emptyList()),
                            "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList()),
                            "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "4.19:EUR", "", null, emptyList()),
                            "articleNumber" to GiniVisionSpecificExtraction("articleNumber", "55789642", "", null, emptyList())
                    )
            )
    ))

    private fun totalLineItemsCount(selectableLineItems: List<SelectableLineItem>) = selectableLineItems.fold(
            0) { c, sli -> c + sli.lineItem.quantity }

    @Before
    fun setUp() {
        initMocks(this)
    }

    @Test
    fun `should show line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            updateView()

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    @Test
    fun `should show selected and total line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            digitalInvoice.selectableLineItems.run {
                first().selected = false
                val unselectedQuantity = first().lineItem.quantity

                updateView()

                // Then
                val totalLineItemsCount = totalLineItemsCount(this)

                verify(view).showSelectedAndTotalLineItems(totalLineItemsCount - unselectedQuantity,
                        totalLineItemsCount)
            }
        }
    }

    @Test
    fun `should enable pay button, if there are selected items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            digitalInvoice.selectableLineItems.run {
                updateView()

                // Then
                val totalLineItemsCount = totalLineItemsCount(this)

                verify(view).enablePayButton(totalLineItemsCount, totalLineItemsCount)
            }
        }
    }

    @Test
    fun `should disable pay button, if there are no selected items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            digitalInvoice.selectableLineItems.run {
                forEach { it.selected = false }
                updateView()

                // Then
                verify(view).disablePayButton(0, totalLineItemsCount(this))
            }
        }
    }

    @Test
    fun `should show selected line items sum when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            updateView()

            // Then
            verify(view).showSelectedLineItemsSum("${Currency.getInstance("EUR").symbol}48",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}56")
        }
    }

    @Test
    fun `should show sum of only selected line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], "Ich will es nicht")

            // When
            updateView()

            // Then
            verify(view).showSelectedLineItemsSum("${Currency.getInstance("EUR").symbol}28",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}58")
        }
    }

    @Test
    fun `should update view on start`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            start()

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    @Test
    fun `should update view when selecting a line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            selectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    open class ViewWithSelectedReturnReason(val reason: String?) : View {
        override fun showLineItems(lineItems: List<SelectableLineItem>) {}
        override fun showSelectedAndTotalLineItems(selected: Int, total: Int) {}
        override fun enablePayButton(selected: Int, total: Int) {}
        override fun disablePayButton(selected: Int, total: Int) {}
        override fun showSelectedLineItemsSum(integralPart: String, fractionalPart: String) {}
        override fun setPresenter(presenter: DigitalInvoiceScreenContract.Presenter) {}

        override fun showReturnReasonDialog(reasons: List<String>,
                                            resultCallback: ReturnReasonDialogResultCallback) {
            resultCallback(reason)
        }
    }

    @Test
    fun `should update view when deselecting a line item after a reason was selected`() {
        // Given
        view = spy(ViewWithSelectedReturnReason("Item is not for me"))

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    @Test
    fun `should update view when deselecting a line item after a reason was not selected`() {
        // Given
        view = spy(ViewWithSelectedReturnReason(null))

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    @Test
    fun `should select line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], "Nem kell")

            // When
            selectLineItem(digitalInvoice.selectableLineItems[0])

            // Then
            assertThat(digitalInvoice.selectableLineItems[0].selected).isTrue()
        }
    }

    @Test
    fun `should show return reason dialog when deselecting a line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            verify(view).showReturnReasonDialog(any(), any())
        }
    }

    @Test
    fun `should deselect line item when a reason was selected`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            assertThat(digitalInvoice.selectableLineItems.first().selected).isFalse()
        }
    }

    @Test
    fun `should invoke edit line item on listener`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            listener = mock()


            // When
            editLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            verify(listener)?.onEditLineItem(digitalInvoice.selectableLineItems.first())
        }
    }

    @Test
    fun `should update line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            val modifiedLineItem = digitalInvoice.selectableLineItems[0]
                    .copy(lineItem = digitalInvoice.selectableLineItems[0].lineItem
                            .copy(description = "Line Item X", quantity = 8, rawGrossPrice = "99.19:EUR"))

            // When
            updateLineItem(modifiedLineItem)

            // Then
            assertThat(digitalInvoice.selectableLineItems.first()).isEqualTo(modifiedLineItem)
        }
    }

    @Test
    fun `should pass return reason to deselected item`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {

            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            assertThat(digitalInvoice.selectableLineItems.first().reason).isEqualTo("Item is not for me")
        }
    }

    @Test
    fun `should not deselect line item when a reason was not selected`() {
        // Given
        view = ViewWithSelectedReturnReason(null)

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            assertThat(digitalInvoice.selectableLineItems.first().selected).isTrue()
        }
    }

    @Test
    fun `should remove reason when a reason was not selected`() {
        // Given
        view = ViewWithSelectedReturnReason(null)

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            assertThat(digitalInvoice.selectableLineItems.first().reason).isNull()
        }
    }

    @Test
    fun `should create LineItems from the 'lineItems' compound extraction`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            // Then
            val specificExtractionMaps = compoundExtractions["lineItems"]?.specificExtractionMaps
            digitalInvoice.selectableLineItems[0].lineItem.run {
                assertThat(description).isEqualTo(specificExtractionMaps?.get(0)?.get("description")?.value)
                assertThat(rawGrossPrice).isEqualTo(specificExtractionMaps?.get(0)?.get("grossPrice")?.value)
                assertThat(quantity.toString()).isEqualTo(specificExtractionMaps?.get(0)?.get("quantity")?.value)
            }
            digitalInvoice.selectableLineItems[1].lineItem.run {
                assertThat(description).isEqualTo(specificExtractionMaps?.get(1)?.get("description")?.value)
                assertThat(rawGrossPrice).isEqualTo(specificExtractionMaps?.get(1)?.get("grossPrice")?.value)
                assertThat(quantity.toString()).isEqualTo(specificExtractionMaps?.get(1)?.get("quantity")?.value)
            }
        }
    }

    @Test
    fun `should return selected and deselected line items when the 'Pay' button was clicked`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            listener = mock()

            deselectLineItem(digitalInvoice.selectableLineItems[0])

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = eq(digitalInvoice.selectableLineItems.takeLast(2).map { it.lineItem }),
                    selectedLineItemsTotalPrice = any(),
                    deselectedLineItems = eq(listOf(digitalInvoice.selectableLineItems[0].lineItem)),
                    reviewedCompoundExtractions = any(),
                    reviewedExtractions = any())
        }
    }

    @Test
    fun `should return total price of selected line items when the 'Pay' button was clicked`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            listener = mock()

            deselectLineItem(digitalInvoice.selectableLineItems[0])

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = any(),
                    selectedLineItemsTotalPrice = eq("28.58:EUR"),
                    deselectedLineItems = any(),
                    reviewedCompoundExtractions = any(),
                    reviewedExtractions = any())
        }
    }

    @Test
    fun `should update amount in extractions when the 'Pay' button was clicked`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        val extractions = mapOf("amountToPay" to GiniVisionSpecificExtraction("amountToPay", "1.99:EUR", "amount", null, emptyList()))

        DigitalInvoiceScreenPresenter(activity, view, extractions, compoundExtractions = createLineItemsFixture()).run {
            listener = mock()

            deselectLineItem(digitalInvoice.selectableLineItems[0])

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = any(),
                    selectedLineItemsTotalPrice = any(),
                    deselectedLineItems = any(),
                    reviewedCompoundExtractions = any(),
                    reviewedExtractions = argThat { get("amountToPay")?.value?.equals("28.58:EUR") ?: false })
        }
    }

    @Test
    fun `should update the 'lineItems' compound extractions when the 'Pay' button was clicked`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            listener = mock()

            deselectLineItem(digitalInvoice.selectableLineItems[0])

            updateLineItem(digitalInvoice.selectableLineItems[1]
                    .copy(lineItem = digitalInvoice.selectableLineItems[1].lineItem
                            .copy(quantity = 99)))

            updateLineItem(digitalInvoice.selectableLineItems[2]
                    .copy(lineItem = digitalInvoice.selectableLineItems[2].lineItem
                            .copy(rawGrossPrice = "203.19:EUR")))

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = any(),
                    selectedLineItemsTotalPrice = any(),
                    deselectedLineItems = any(),
                    reviewedCompoundExtractions = argThat {
                        val specificExtractionMaps = get("lineItems")?.specificExtractionMaps
                        specificExtractionMaps?.size == 2
                                && specificExtractionMaps[0]["quantity"]?.value.equals("99")
                                && specificExtractionMaps[1]["grossPrice"]?.value.equals("203.19:EUR")
                    },
                    reviewedExtractions = any())
        }
    }

    @Test
    fun `should return empty 'lineItems' compound extraction, if all line items were deselected, when the 'Pay' button was clicked`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = createLineItemsFixture()).run {
            listener = mock()

            digitalInvoice.selectableLineItems.forEach { it.selected = false }

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = any(),
                    selectedLineItemsTotalPrice = any(),
                    deselectedLineItems = any(),
                    reviewedCompoundExtractions = argThat { get("lineItems")?.specificExtractionMaps?.size == 0 },
                    reviewedExtractions = any())
        }
    }

}
