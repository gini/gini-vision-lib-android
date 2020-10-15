package net.gini.android.vision.digitalinvoice

import android.app.Activity
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import net.gini.android.vision.OncePerInstallEvent
import net.gini.android.vision.OncePerInstallEventStore
import net.gini.android.vision.digitalinvoice.DigitalInvoiceScreenContract.View
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionReturnReason
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
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

    @Mock
    private lateinit var oncePerInstallEventStore: OncePerInstallEventStore

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

    private val returnReasonsFixture = listOf(
            GiniVisionReturnReason("r1", mapOf("de" to "Andere farbe als beworben")),
            GiniVisionReturnReason("r2", mapOf("de" to "Schlechte Qualität")),
            GiniVisionReturnReason("r3", mapOf("de" to "Passt nicht")),
            GiniVisionReturnReason("r4", mapOf("de" to "Gefällt nicht")),
            GiniVisionReturnReason("r5", mapOf("de" to "Falsches Artikel")),
            GiniVisionReturnReason("r6", mapOf("de" to "Beschädigt")),
            GiniVisionReturnReason("r7", mapOf("de" to "Zu spät geliefert")),
    )

    private fun totalLineItemsCount(selectableLineItems: List<SelectableLineItem>) = selectableLineItems.fold(
            0) { c, sli -> c + sli.lineItem.quantity }

    @Before
    fun setUp() {
        initMocks(this)
    }

    @Test
    fun `should show line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // When
            updateView()

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    @Test
    fun `should show selected and total line items when updating the view`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
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
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
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
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
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
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
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
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], GiniVisionReturnReason("r1", mapOf("de" to "Ich will es nicht")))

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
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // When
            start()

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    @Test
    fun `should update view when selecting a line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // When
            selectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    open class ViewWithSelectedReturnReason(val reason: GiniVisionReturnReason?) : View {
        override fun showLineItems(lineItems: List<SelectableLineItem>) {}
        override fun showSelectedAndTotalLineItems(selected: Int, total: Int) {}
        override fun showAddons(addons: List<DigitalInvoiceAddon>) {}
        override fun enablePayButton(selected: Int, total: Int) {}
        override fun disablePayButton(selected: Int, total: Int) {}
        override fun showSelectedLineItemsSum(integralPart: String, fractionalPart: String) {}
        override fun setPresenter(presenter: DigitalInvoiceScreenContract.Presenter) {}
        override fun showOnboarding() {}

        override fun showReturnReasonDialog(reasons: List<GiniVisionReturnReason>,
                                            resultCallback: ReturnReasonDialogResultCallback) {
            resultCallback(reason)
        }
    }

    @Test
    fun `should update view when deselecting a line item after a reason was selected`() {
        // Given
        view = spy(ViewWithSelectedReturnReason(GiniVisionReturnReason("r1", mapOf("de" to "Hässlich"))))

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore,
                returnReasons = returnReasonsFixture)
                .run {
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

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore,
                returnReasons = returnReasonsFixture)
                .run {
                    // When
                    deselectLineItem(digitalInvoice.selectableLineItems.first())

                    // Then
                    verify(view).showLineItems(digitalInvoice.selectableLineItems)
                }
    }

    @Test
    fun `should select line item`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            digitalInvoice.deselectLineItem(digitalInvoice.selectableLineItems[0], GiniVisionReturnReason("r1", mapOf("de" to "Pfui")))

            // When
            selectLineItem(digitalInvoice.selectableLineItems[0])

            // Then
            assertThat(digitalInvoice.selectableLineItems[0].selected).isTrue()
        }
    }

    @Test
    fun `should show return reason dialog when deselecting a line item, if there are return reasons`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore,
                returnReasons = returnReasonsFixture)
                .run {
                    // When
                    deselectLineItem(digitalInvoice.selectableLineItems.first())

                    // Then
                    verify(view).showReturnReasonDialog(any(), any())
                }
    }

    @Test
    fun `should deselect line item when a reason was selected`() {
        // Given
        view = ViewWithSelectedReturnReason(GiniVisionReturnReason("r1", mapOf("de" to "Brauch ich nicht")))

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            assertThat(digitalInvoice.selectableLineItems.first().selected).isFalse()
        }
    }

    @Test
    fun `should not show return reason dialog, if there are no return reasons`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            assertThat(digitalInvoice.selectableLineItems.first().selected).isFalse()
        }
    }

    @Test
    fun `should update view when deselecting a line item, if there are no return reasons`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            verify(view).showLineItems(digitalInvoice.selectableLineItems)
        }
    }

    @Test
    fun `should invoke edit line item on listener`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
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
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
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
        view = ViewWithSelectedReturnReason(GiniVisionReturnReason("r1", mapOf("de" to "Hässlich")))

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore,
                returnReasons = returnReasonsFixture)
                .run {
                    // When
                    deselectLineItem(digitalInvoice.selectableLineItems.first())

                    // Then
                    assertThat(digitalInvoice.selectableLineItems.first().reason?.labelInLocalLanguageOrGerman).isEqualTo("Hässlich")
                }
    }

    @Test
    fun `should not deselect line item when a reason was not selected`() {
        // Given
        view = ViewWithSelectedReturnReason(null)

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore,
                returnReasons = returnReasonsFixture)
                .run { // When
                    deselectLineItem(digitalInvoice.selectableLineItems.first())

                    // Then
                    assertThat(digitalInvoice.selectableLineItems.first().selected).isTrue()
        }
    }

    @Test
    fun `should remove reason when a reason was not selected`() {
        // Given
        view = ViewWithSelectedReturnReason(null)

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // When
            deselectLineItem(digitalInvoice.selectableLineItems.first())

            // Then
            assertThat(digitalInvoice.selectableLineItems.first().reason).isNull()
        }
    }

    @Test
    fun `should create LineItems from the 'lineItems' compound extraction`() {
        // Given
        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            // Then
            val specificExtractionMaps = compoundExtractions["lineItems"]?.specificExtractionMaps
            digitalInvoice.selectableLineItems[0].lineItem.run {
                assertThat(description).isEqualTo(specificExtractionMaps?.get(0)?.get("description")?.value)
                assertThat(rawGrossPrice).isEqualTo(specificExtractionMaps?.get(0)?.get("baseGross")?.value)
                assertThat(quantity.toString()).isEqualTo(specificExtractionMaps?.get(0)?.get("quantity")?.value)
            }
            digitalInvoice.selectableLineItems[1].lineItem.run {
                assertThat(description).isEqualTo(specificExtractionMaps?.get(1)?.get("description")?.value)
                assertThat(rawGrossPrice).isEqualTo(specificExtractionMaps?.get(1)?.get("baseGross")?.value)
                assertThat(quantity.toString()).isEqualTo(specificExtractionMaps?.get(1)?.get("quantity")?.value)
            }
        }
    }

    @Test
    fun `should update amount in extractions when the 'Pay' button was clicked`() {
        // Given
        view = ViewWithSelectedReturnReason(GiniVisionReturnReason("r1", mapOf("de" to "Qualitätsmängel")))

        val extractions = mapOf("amountToPay" to GiniVisionSpecificExtraction("amountToPay", "1.99:EUR", "amount", null, emptyList()))

        DigitalInvoiceScreenPresenter(activity, view, extractions,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            listener = mock()

            deselectLineItem(digitalInvoice.selectableLineItems[0])

            // When
            pay()

            // Then
            verify(listener)?.onPayInvoice(specificExtractions = argThat { get("amountToPay")?.value?.equals("28.58:EUR") ?: false },
                    compoundExtractions = any())
        }
    }

    @Test
    fun `should update the 'lineItems' compound extractions when the 'Pay' button was clicked`() {
        // Given
        view = ViewWithSelectedReturnReason(GiniVisionReturnReason("r1", mapOf("de" to "Pfui Deifi")))

        DigitalInvoiceScreenPresenter(activity, view, 
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
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
            verify(listener)?.onPayInvoice(specificExtractions = any(),
                    compoundExtractions = argThat {
                        val specificExtractionMaps = get("lineItems")?.specificExtractionMaps
                        specificExtractionMaps?.size == 3
                                && specificExtractionMaps[0]["quantity"]?.value.equals("0")
                                && specificExtractionMaps[1]["quantity"]?.value.equals("99")
                                && specificExtractionMaps[2]["baseGross"]?.value.equals("203.19:EUR")
                    })
        }
    }

    @Test
    fun `should show onboarding on first run`() {
        // Given
        `when`(oncePerInstallEventStore.containsEvent(eq(OncePerInstallEvent.SHOW_DIGITAL_INVOICE_ONBOARDING)))
                .thenReturn(false)

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            listener = mock()

            // When
            start()

            // Then
            verify(view).showOnboarding()
        }
    }

    @Test
    fun `should not show onboarding on subsequent runs`() {
        // Given
        `when`(oncePerInstallEventStore.containsEvent(eq(OncePerInstallEvent.SHOW_DIGITAL_INVOICE_ONBOARDING)))
                .thenReturn(true)

        DigitalInvoiceScreenPresenter(activity, view,
                compoundExtractions = createLineItemsFixture(),
                oncePerInstallEventStore = oncePerInstallEventStore).run {
            listener = mock()

            // When
            start()

            // Then
            verify(view, never()).showOnboarding()
        }
    }

}
