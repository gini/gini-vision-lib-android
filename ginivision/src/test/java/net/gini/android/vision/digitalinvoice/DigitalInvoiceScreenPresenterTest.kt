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
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.initMocks
import java.util.*
import kotlin.random.Random


/**
 * Created by Alpar Szotyori on 12.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val mockLineItems = List(5) { i ->
    LineItem(id = "$i",
            description = "Nike Sportswear Air Max ${Random.nextInt(1, 50)} - Sneaker Low",
            quantity = Random.nextInt(1, 5),
            rawAmount = "${Random.nextInt(50)}.${Random.nextInt(9)}${Random.nextInt(9)}:EUR")
}.map { SelectableLineItem(lineItem = it) }

@RunWith(JUnit4::class)
class DigitalInvoiceScreenPresenterTest {

    @Mock
    private lateinit var activity: Activity
    @Mock
    private lateinit var view: View

    private val totalLineItemsCount = mockLineItems.fold(0) { c, sli -> c + sli.lineItem.quantity }

    @Before
    fun setUp() {
        initMocks(this)
    }

    @Test
    fun `should show line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            updateView(lineItems)

            // Then
            verify(view).showLineItems(lineItems)
        }
    }

    @Test
    fun `should show selected and total line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            lineItems.run {
                first().selected = false
                val unselectedQuantity = first().lineItem.quantity

                updateView(this)

                // Then
                verify(view).showSelectedAndTotalLineItems(totalLineItemsCount - unselectedQuantity,
                        totalLineItemsCount)
            }
        }
    }

    @Test
    fun `should enable pay button, if there are selected items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            lineItems.run {
                updateView(this)

                // Then
                verify(view).enablePayButton(totalLineItemsCount, totalLineItemsCount)
            }
        }
    }

    @Test
    fun `should disable pay button, if there are no selected items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            lineItems.run {
                forEach { it.selected = false }
                updateView(this)

                // Then
                verify(view).disablePayButton(0, totalLineItemsCount)
            }
        }
    }

    @Test
    fun `should show selected line items sum when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 1,
                                    rawAmount = "1.09:EUR")),
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "2", description = "Line Item 2", quantity = 2,
                                    rawAmount = "2.99:EUR"))
            )

            // When
            updateView(lineItems)

            // Then
            verify(view).showSelectedLineItemsSum("${Currency.getInstance("EUR").symbol}7",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}07")
        }
    }

    @Test
    fun `should show sum of only selected line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = false,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 1,
                                    rawAmount = "1.09:EUR")),
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "2", description = "Line Item 2", quantity = 2,
                                    rawAmount = "2.99:EUR")),
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "3", description = "Line Item 3", quantity = 3,
                                    rawAmount = "3.10:EUR"))
            )

            // When
            updateView(lineItems)

            // Then
            verify(view).showSelectedLineItemsSum("${Currency.getInstance("EUR").symbol}15",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}28")
        }
    }

    @Test
    fun `should update view on start`() {
        // Given
        spy(DigitalInvoiceScreenPresenter(activity, view)).run {
            // When
            start()

            // Then
            verify(view).showLineItems(lineItems)
        }
    }

    @Test
    fun `should update view when selecting a line item`() {
        // Given
        spy(DigitalInvoiceScreenPresenter(activity, view)).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            selectLineItem(lineItems.first())

            // Then
            verify(view).showLineItems(lineItems)
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

        spy(DigitalInvoiceScreenPresenter(activity, view)).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            deselectLineItem(lineItems.first())

            // Then
            verify(view).showLineItems(lineItems)
        }
    }

    @Test
    fun `should update view when deselecting a line item after a reason was not selected`() {
        // Given
        view = spy(ViewWithSelectedReturnReason(null))

        spy(DigitalInvoiceScreenPresenter(activity, view)).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            deselectLineItem(lineItems.first())

            // Then
            verify(view).showLineItems(lineItems)
        }
    }

    @Test
    fun `should select line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = false,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 2,
                                    rawAmount = "1.19:EUR"))
            )

            // When
            selectLineItem(lineItems.first())

            // Then
            assertThat(lineItems.first().selected).isTrue()
        }
    }

    @Test
    fun `should show return reason dialog when deselecting a line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = mockLineItems.map { it.copy() }

            // When
            deselectLineItem(lineItems.first())

            // Then
            verify(view).showReturnReasonDialog(any(), any())
        }
    }

    @Test
    fun `should deselect line item when a reason was selected`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                                    rawAmount = "1.19:EUR"))
            )

            // When
            deselectLineItem(lineItems.first())

            // Then
            assertThat(lineItems.first().selected).isFalse()
        }
    }

    @Test
    fun `should invoke edit line item on listener`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = mockLineItems.map { it.copy() }
            listener = mock(DigitalInvoiceFragmentListener::class.java)


            // When
            editLineItem(lineItems.first())

            // Then
            verify(listener)?.onEditLineItem(lineItems.first())
        }
    }

    @Test
    fun `should update line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                                    rawAmount = "1.19:EUR"))
            )

            val modifiedLineItem = SelectableLineItem(selected = true,
                    lineItem = LineItem(id = "1", description = "Line Item X", quantity = 8,
                            rawAmount = "99.19:EUR"))

            // When
            updateLineItem(modifiedLineItem)

            // Then
            assertThat(lineItems.first()).isEqualTo(modifiedLineItem)
        }
    }

    @Test
    fun `should pass return reason to deselected item`() {
        // Given
        view = ViewWithSelectedReturnReason("Item is not for me")

        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                                    rawAmount = "1.19:EUR"))
            )

            // When
            deselectLineItem(lineItems.first())

            // Then
            assertThat(lineItems.first().reason).isEqualTo("Item is not for me")
        }
    }

    @Test
    fun `should not deselect line item when a reason was not selected`() {
        // Given
        view = ViewWithSelectedReturnReason(null)

        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                                    rawAmount = "1.19:EUR"))
            )

            // When
            deselectLineItem(lineItems.first())

            // Then
            assertThat(lineItems.first().selected).isTrue()
        }
    }

    @Test
    fun `should remove reason when a reason was not selected`() {
        // Given
        view = ViewWithSelectedReturnReason(null)

        DigitalInvoiceScreenPresenter(activity, view).run {
            lineItems = listOf(
                    SelectableLineItem(selected = true,
                            lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                                    rawAmount = "1.19:EUR"))
            )

            // When
            deselectLineItem(lineItems.first())

            // Then
            assertThat(lineItems.first().reason).isNull()
        }
    }

    @Test
    fun `should create LineItems from the 'lineItems' compound extraction`() {
        // Given
        val compoundExtractions =
                mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                        listOf(
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Foo Bar", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "1.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Zoo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "2.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "4", "", null, emptyList())
                                )
                        )
                ))

        // Then
        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = compoundExtractions).run {
            val specificExtractionMaps = compoundExtractions["lineItems"]?.specificExtractionMaps
            lineItems[0].lineItem.run {
                assertThat(description).isEqualTo(specificExtractionMaps?.get(0)?.get("description")?.value)
                assertThat(rawAmount).isEqualTo(specificExtractionMaps?.get(0)?.get("grossPrice")?.value)
                assertThat(quantity.toString()).isEqualTo(specificExtractionMaps?.get(0)?.get("quantity")?.value)
            }
            lineItems[1].lineItem.run {
                assertThat(description).isEqualTo(specificExtractionMaps?.get(1)?.get("description")?.value)
                assertThat(rawAmount).isEqualTo(specificExtractionMaps?.get(1)?.get("grossPrice")?.value)
                assertThat(quantity.toString()).isEqualTo(specificExtractionMaps?.get(1)?.get("quantity")?.value)
            }
        }
    }

    @Test
    fun `should return selected and deselected line items when the 'Pay' button was clicked`() {
        // Given
        val compoundExtractions =
                mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                        listOf(
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Foo Bar", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "1.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Zoo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "2.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "4", "", null, emptyList())
                                )
                        )
                ))

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = compoundExtractions).run {
            listener = mock(DigitalInvoiceFragmentListener::class.java)

            lineItems[1].selected = false

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = eq(listOf(lineItems[0].lineItem)),
                    selectedLineItemsTotalPrice = any(),
                    deselectedLineItems = eq(listOf(lineItems[1].lineItem)),
                    reviewedCompoundExtractions = any(),
                    reviewedExtractions = any())
        }
    }

    @Test
    fun `should return total price of selected line items when the 'Pay' button was clicked`() {
        // Given
        val compoundExtractions =
                mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                        listOf(
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Foo Bar", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "1.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Zoo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "2.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "4", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Foo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "4.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList())
                                )
                        )
                ))

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = compoundExtractions).run {
            listener = mock(DigitalInvoiceFragmentListener::class.java)

            lineItems[0].selected = false

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = any(),
                    selectedLineItemsTotalPrice = eq("16.95:EUR"),
                    deselectedLineItems = any(),
                    reviewedCompoundExtractions = any(),
                    reviewedExtractions = any())
        }
    }

    @Test
    fun `should update amount in extractions when the 'Pay' button was clicked`() {
        // Given
        val extractions = mapOf("amountToPay" to GiniVisionSpecificExtraction("amountToPay", "10.00:EUR", "", null, emptyList()))

        val compoundExtractions =
                mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                        listOf(
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Foo Bar", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "1.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Zoo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "2.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "4", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Foo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "4.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList())
                                )
                        )
                ))

        DigitalInvoiceScreenPresenter(activity, view, extractions, compoundExtractions).run {
            listener = mock(DigitalInvoiceFragmentListener::class.java)

            lineItems[0].selected = false

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(
                    selectedLineItems = any(),
                    selectedLineItemsTotalPrice = any(),
                    deselectedLineItems = any(),
                    reviewedCompoundExtractions = any(),
                    reviewedExtractions = argThat { get("amountToPay")?.value?.equals("16.95:EUR") ?: false })
        }
    }

    @Test
    fun `should update the 'lineItems' compound extractions when the 'Pay' button was clicked`() {
        // Given
        val compoundExtractions =
                mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                        listOf(
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Foo Bar", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "1.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Zoo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "2.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "4", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Foo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "4.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList())
                                )
                        )
                ))

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = compoundExtractions).run {
            listener = mock(DigitalInvoiceFragmentListener::class.java)

            lineItems[0].selected = false

            lineItems = lineItems.mapIndexed { index, sli ->
                if (index == 1) sli.copy(lineItem = sli.lineItem.copy(quantity = 2)) else sli
            }

            lineItems = lineItems.mapIndexed { index, sli ->
                if (index == 2) sli.copy(lineItem = sli.lineItem.copy(rawAmount = "10.19:EUR")) else sli
            }

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
                                && specificExtractionMaps[0]["quantity"]?.value.equals("2") ?: false
                                && specificExtractionMaps[1]["grossPrice"]?.value.equals("10.19:EUR") ?: false
                    },
                    reviewedExtractions = any())
        }
    }

    @Test
    fun `should return empty 'lineItems' compound extraction, if all line items were deselected, when the 'Pay' button was clicked`() {
        // Given
        val compoundExtractions =
                mapOf("lineItems" to GiniVisionCompoundExtraction("lineItems",
                        listOf(
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Foo Bar", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "1.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "2", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Zoo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "2.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "4", "", null, emptyList())
                                ),
                                mapOf(
                                        "description" to GiniVisionSpecificExtraction("description", "Bar Foo", "", null, emptyList()),
                                        "grossPrice" to GiniVisionSpecificExtraction("grossPrice", "4.99:EUR", "", null, emptyList()),
                                        "quantity" to GiniVisionSpecificExtraction("quantity", "1", "", null, emptyList())
                                )
                        )
                ))

        DigitalInvoiceScreenPresenter(activity, view, compoundExtractions = compoundExtractions).run {
            listener = mock(DigitalInvoiceFragmentListener::class.java)

            lineItems.forEach { it.selected = false }

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
