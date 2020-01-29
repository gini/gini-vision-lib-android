package net.gini.android.vision.returnassistant.details

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import net.gini.android.vision.returnassistant.DialogResultCallback
import net.gini.android.vision.returnassistant.FRACTION_FORMAT
import net.gini.android.vision.returnassistant.LineItem
import net.gini.android.vision.returnassistant.SelectableLineItem
import net.gini.android.vision.returnassistant.details.LineItemDetailsScreenContract.View
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations.initMocks
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
    private lateinit var view: View

    private val decimalFormat = DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(
            Locale.ENGLISH)).apply { isParseBigDecimal = true }

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
        LineItemDetailsScreenPresenter(activity, view, sli, decimalFormat).run {
            // When
            start()

            // Then
            verify(view).showCheckbox(true, 3)
            verify(view).showDescription("Line Item 1")
            verify(view).showQuantity(3)
            verify(view).showAmount("1.19", Currency.getInstance("EUR").symbol)
            verify(view).showTotalAmount("${Currency.getInstance("EUR").symbol}3",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}57")
            verify(view).disableSaveButton()
        }
    }

    @Test
    fun `should show amount formatted`() {
        // Given
        val germanDecimalFormat =
                DecimalFormat("0.00", DecimalFormatSymbols.getInstance(
                        Locale.GERMAN)).apply { isParseBigDecimal = true }
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, germanDecimalFormat).run {
            // When
            start()

            // Then
            verify(view).showAmount("1,19", Currency.getInstance("EUR").symbol)
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

    open class ViewWithSelectedReturnReason(val reason: String?) : View {
        override fun showDescription(description: String) {}
        override fun showQuantity(quantity: Int) {}
        override fun showAmount(amount: String, currency: String) {}
        override fun showCheckbox(selected: Boolean, quantity: Int) {}
        override fun showTotalAmount(integralPart: String, fractionPart: String) {}
        override fun enableSaveButton() {}
        override fun disableSaveButton() {}
        override fun enableInput() {}
        override fun disableInput() {}
        override fun setPresenter(presenter: LineItemDetailsScreenContract.Presenter) {}

        override fun showReturnReasonDialog(reasons: List<String>,
                                            resultCallback: DialogResultCallback) {
            resultCallback(reason)
        }
    }

    @Test
    fun `should deselect line item when a reason was selected`() {
        // Given
        val view = spy(ViewWithSelectedReturnReason("Item is not for me"))

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
    fun `should pass return reason to deselected item`() {
        // Given
        val view = spy(ViewWithSelectedReturnReason("Item is not for me"))

        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            deselectLineItem()

            // Then
            assertThat(selectableLineItem.reason).isEqualTo("Item is not for me")
        }
    }

    @Test
    fun `should not deselect line item when a reason was not selected`() {
        // Given
        val view = spy(ViewWithSelectedReturnReason(null))

        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            deselectLineItem()

            // Then
            verify(view).enableInput()
            verify(view).showCheckbox(true, 3)
            verify(view).disableSaveButton()
        }
    }

    @Test
    fun `should remove reason when a reason was not selected`() {
        // Given
        val view = spy(ViewWithSelectedReturnReason(null))

        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            deselectLineItem()

            // Then
            assertThat(selectableLineItem.reason).isNull()
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
        LineItemDetailsScreenPresenter(activity, view, sli, decimalFormat).run {
            // When
            setAmount("19.99")

            // Then
            assertThat(selectableLineItem.lineItem.amount).isEqualTo(BigDecimal("19.99"))
            verify(view).showTotalAmount("${Currency.getInstance("EUR").symbol}59",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}97")
            verify(view).enableSaveButton()
        }
    }

    @Test
    fun `should parse amount in the format it was shown with`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawAmount = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, decimalFormat).run {
            // When
            start()
            // Using german format (comma decimal separator)
            setAmount("200,19")

            // Then
            // Since we used english format, the comma was interpreted as a grouping separator
            assertThat(selectableLineItem.lineItem.amount).isEqualTo(BigDecimal("20019.00"))
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