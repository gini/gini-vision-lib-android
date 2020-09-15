package net.gini.android.vision.digitalinvoice.details

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.verify
import net.gini.android.vision.digitalinvoice.FRACTION_FORMAT
import net.gini.android.vision.digitalinvoice.LineItem
import net.gini.android.vision.digitalinvoice.ReturnReasonDialogResultCallback
import net.gini.android.vision.digitalinvoice.SelectableLineItem
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsScreenContract.View
import net.gini.android.vision.network.model.GiniVisionReturnReason
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

    private val returnReasonsFixture = listOf(
            GiniVisionReturnReason("r1", mapOf("de" to "Andere farbe als beworben")),
            GiniVisionReturnReason("r2", mapOf("de" to "Schlechte Qualität")),
            GiniVisionReturnReason("r3", mapOf("de" to "Passt nicht")),
            GiniVisionReturnReason("r4", mapOf("de" to "Gefällt nicht")),
            GiniVisionReturnReason("r5", mapOf("de" to "Falsches Artikel")),
            GiniVisionReturnReason("r6", mapOf("de" to "Beschädigt")),
            GiniVisionReturnReason("r7", mapOf("de" to "Zu spät geliefert")),
    )

    @Before
    fun setUp() {
        initMocks(this)
    }

    @Test
    fun `should show line item`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, grossPriceFormat = decimalFormat).run {
            // When
            start()

            // Then
            verify(view).showCheckbox(true, 3)
            verify(view).showDescription("Line Item 1")
            verify(view).showQuantity(3)
            verify(view).showGrossPrice("1.19", Currency.getInstance("EUR").symbol)
            verify(view).showTotalGrossPrice("${Currency.getInstance("EUR").symbol}3",
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
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, grossPriceFormat = germanDecimalFormat).run {
            // When
            start()

            // Then
            verify(view).showGrossPrice("1,19", Currency.getInstance("EUR").symbol)
        }
    }

    @Test
    fun `should select line item`() {
        // Given
        val sli = SelectableLineItem(selected = false,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            selectLineItem()

            // Then
            verify(view).enableInput()
            verify(view).showCheckbox(true, 3)
            verify(view).enableSaveButton()
        }
    }

    open class ViewWithSelectedReturnReason(val reason: GiniVisionReturnReason?) : View {
        override fun showDescription(description: String) {}
        override fun showQuantity(quantity: Int) {}
        override fun showGrossPrice(displayedGrossPrice: String, currency: String) {}
        override fun showCheckbox(selected: Boolean, quantity: Int) {}
        override fun showTotalGrossPrice(integralPart: String, fractionalPart: String) {}
        override fun enableSaveButton() {}
        override fun disableSaveButton() {}
        override fun enableInput() {}
        override fun disableInput() {}
        override fun setPresenter(presenter: LineItemDetailsScreenContract.Presenter) {}

        override fun showReturnReasonDialog(reasons: List<GiniVisionReturnReason>,
                                            resultCallback: ReturnReasonDialogResultCallback) {
            resultCallback(reason)
        }
    }

    @Test
    fun `should deselect line item when a reason was selected, if there are return reasons`() {
        // Given
        val view = spy(ViewWithSelectedReturnReason(GiniVisionReturnReason("r1", mapOf("de" to "Pfui Deifi"))))

        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, returnReasons = returnReasonsFixture).run {
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
        val view = spy(ViewWithSelectedReturnReason(GiniVisionReturnReason("r1", mapOf("de" to "Pfui Deifi"))))

        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, returnReasons = returnReasonsFixture).run { // When
            deselectLineItem()

            // Then
            assertThat(selectableLineItem.reason?.labelInLocalLanguageOrGerman).isEqualTo("Pfui Deifi")
        }
    }

    @Test
    fun `should not deselect line item when a reason was not selected`() {
        // Given
        val view = spy(ViewWithSelectedReturnReason(null))

        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, returnReasons = returnReasonsFixture).run { // When
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
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            deselectLineItem()

            // Then
            assertThat(selectableLineItem.reason).isNull()
        }
    }

    @Test
    fun `should not show return reason dialog, if there are no return reasons`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
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
                        rawGrossPrice = "1.19:EUR"))
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
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            setQuantity(99)

            // Then
            assertThat(selectableLineItem.lineItem.quantity).isEqualTo(99)
            verify(view).showTotalGrossPrice("${Currency.getInstance("EUR").symbol}117",
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
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, grossPriceFormat = decimalFormat).run {
            // When
            setGrossPrice("19.99")

            // Then
            assertThat(selectableLineItem.lineItem.grossPrice).isEqualTo(BigDecimal("19.99"))
            verify(view).showTotalGrossPrice("${Currency.getInstance("EUR").symbol}59",
                    "${FRACTION_FORMAT.decimalFormatSymbols.decimalSeparator}97")
            verify(view).enableSaveButton()
        }
    }

    @Test
    fun `should parse amount in the format it was shown with`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli, grossPriceFormat = decimalFormat).run {
            // When
            start()
            // Using german format (comma decimal separator)
            setGrossPrice("200,19")

            // Then
            // Since we used english format, the comma was interpreted as a grouping separator
            assertThat(selectableLineItem.lineItem.grossPrice).isEqualTo(BigDecimal("20019.00"))
        }
    }

    @Test
    fun `should invoke save on listener`() {
        // Given
        val sli = SelectableLineItem(selected = true,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            listener = mock(LineItemDetailsFragmentListener::class.java)
            save()

            // Then
            verify(listener)?.onSave(selectableLineItem)
        }
    }

    @Test
    fun `should remove reason when line item is selected`() {
        // Given
        val sli = SelectableLineItem(selected = false,
                lineItem = LineItem(id = "1", description = "Line Item 1", quantity = 3,
                        rawGrossPrice = "1.19:EUR"))
        LineItemDetailsScreenPresenter(activity, view, sli).run {
            // When
            selectLineItem()

            // Then
            assertThat(selectableLineItem.reason).isNull()
        }
    }
}