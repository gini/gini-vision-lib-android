package net.gini.android.vision.returnassistant.details

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.verify
import net.gini.android.vision.returnassistant.FRACTION_FORMAT
import net.gini.android.vision.returnassistant.LineItem
import net.gini.android.vision.returnassistant.SelectableLineItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.initMocks
import java.math.BigDecimal
import java.util.*

/**
 * Created by Alpar Szotyori on 21.01.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

@RunWith(JUnit4::class)
class LineItemDetailsScreenPresenterTest {

    @Mock
    private lateinit var activity: Activity
    @Mock
    private lateinit var view: LineItemDetailsScreenContract.View

    @Before
    fun setUp() {
        initMocks(this)
    }

    @Test
    fun `should show line item`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            start()

            // Then
            verify(view).showCheckbox(true, 3)
            verify(view).showDescription("Line Item 1")
            verify(view).showQuantity(3)
            verify(view).showAmount(BigDecimal("1.19"), Currency.getInstance("EUR").symbol)
            verify(view).showTotalAmount("${Currency.getInstance("EUR").symbol}3",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}57")
            verify(view).disableSaveButton()
        }
    }

    @Test
    fun `should select line item`() {
        // Given
        val sli = SelectableLineItem(selected = false,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            selectLineItem()

            // Then
            verify(view).enableInput()
            verify(view).showCheckbox(true, 3)
            verify(view).enableSaveButton()
        }
    }

    @Test
    fun `should deselect line item`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            deselectLineItem()

            // Then
            verify(view).disableInput()
            verify(view).showCheckbox(false, 3)
            verify(view).enableSaveButton()
        }
    }

    @Test
    fun `should update description`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            setDescription("Line Item X")

            // Then
            assertThat(selectableLineItem.lineItem.description).isEqualTo("Line Item X")
            verify(view).enableSaveButton()
        }
    }

    @Test
    fun `should update quantity`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            setQuantity(99)

            // Then
            assertThat(selectableLineItem.lineItem.quantity).isEqualTo(99)
            verify(view).showTotalAmount("${Currency.getInstance("EUR").symbol}117",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}81")
            verify(view).showCheckbox(true, 99)
            verify(view).enableSaveButton()
        }
    }

    @Test
    fun `should update amount`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            setAmount(BigDecimal("19.99"))

            // Then
            assertThat(selectableLineItem.lineItem.amount).isEqualTo(BigDecimal("19.99"))
            verify(view).showTotalAmount("${Currency.getInstance("EUR").symbol}59",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}97")
            verify(view).enableSaveButton()
        }
    }

    @Test
    fun `should invoke save on listener`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            listener = mock(LineItemDetailsFragmentListener::class.java)
            save()

            // Then
            verify(listener)?.onSave(selectableLineItem)
        }
    }
}