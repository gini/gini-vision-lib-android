package net.gini.android.vision.returnassistant

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import net.gini.android.vision.returnassistant.ReturnAssistantScreenContract.View
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations.initMocks


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
                updateView(this)

                // Then
                verify(view).showSelectedAndTotalLineItems(size - 1, size)
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
                verify(view).enablePayButton(size, size)
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
                verify(view).disablePayButton(0, size)
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
            verify(view).showSelectedLineItemsSum("EUR7", ".07")
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
            verify(view).showSelectedLineItemsSum("EUR15", ".28")
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

    @Test
    fun `should update view when deselecting a line item`() {
        // Given
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
    fun `should deselect line item`() {
        // Given
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
}