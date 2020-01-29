package net.gini.android.vision.returnassistant

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import net.gini.android.vision.returnassistant.ReturnAssistantScreenContract.View
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.initMocks
import java.util.*


/**
 * Created by Alpar Szotyori on 12.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@RunWith(JUnit4::class)
class ReturnAssistantScreenPresenterTest {

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
        ReturnAssistantScreenPresenter(activity, view).run {
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
        ReturnAssistantScreenPresenter(activity, view).run {
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
        ReturnAssistantScreenPresenter(activity, view).run {
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
        ReturnAssistantScreenPresenter(activity, view).run {
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
        ReturnAssistantScreenPresenter(activity, view).run {
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
        ReturnAssistantScreenPresenter(activity, view).run {
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
        spy(ReturnAssistantScreenPresenter(activity, view)).run {
            // When
            start()

            // Then
            verify(view).showLineItems(lineItems)
        }
    }

    @Test
    fun `should update view when selecting a line item`() {
        // Given
        spy(ReturnAssistantScreenPresenter(activity, view)).run {
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
        override fun showSelectedLineItemsSum(integralPart: String, fractionPart: String) {}
        override fun setPresenter(presenter: ReturnAssistantScreenContract.Presenter) {}

        override fun showReturnReasonDialog(reasons: List<String>,
                                            resultCallback: DialogResultCallback) {
            resultCallback(reason)
        }
    }

    @Test
    fun `should update view when deselecting a line item after a reason was selected`() {
        // Given
        view = spy(ViewWithSelectedReturnReason("Item is not for me"))

        spy(ReturnAssistantScreenPresenter(activity, view)).run {
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

        spy(ReturnAssistantScreenPresenter(activity, view)).run {
            // When
            deselectLineItem(lineItems.first())

            // Then
            verify(view).showLineItems(lineItems)
        }
    }

    @Test
    fun `should select line item`() {
        // Given
        ReturnAssistantScreenPresenter(activity, view).run {
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
        ReturnAssistantScreenPresenter(activity, view).run {
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

        ReturnAssistantScreenPresenter(activity, view).run {
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
        ReturnAssistantScreenPresenter(activity, view).run {
            lineItems = mockLineItems.map { it.copy() }
            listener = mock(ReturnAssistantFragmentListener::class.java)


            // When
            editLineItem(lineItems.first())

            // Then
            verify(listener)?.onEditLineItem(lineItems.first())
        }
    }

    @Test
    fun `should update line item`() {
        // Given
        ReturnAssistantScreenPresenter(activity, view).run {
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

        ReturnAssistantScreenPresenter(activity, view).run {
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

        ReturnAssistantScreenPresenter(activity, view).run {
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

        ReturnAssistantScreenPresenter(activity, view).run {
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
}
