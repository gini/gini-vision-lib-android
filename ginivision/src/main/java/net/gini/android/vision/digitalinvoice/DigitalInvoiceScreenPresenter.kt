package net.gini.android.vision.digitalinvoice

import android.app.Activity
import android.support.annotation.VisibleForTesting
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val mockReasons = listOf(
        "Looks different than site image",
        "Poor quality/fault",
        "Doesn't fit properly",
        "Doesn't suite me",
        "Received wrong item",
        "Parcel damaged",
        "Arrived too late"
)

internal open class DigitalInvoiceScreenPresenter(activity: Activity,
                                                  view: DigitalInvoiceScreenContract.View,
                                                  val extractions: Map<String, GiniVisionSpecificExtraction> = emptyMap(),
                                                  val compoundExtractions: Map<String, GiniVisionCompoundExtraction> = emptyMap()) :
        DigitalInvoiceScreenContract.Presenter(activity, view) {

    override var listener: DigitalInvoiceFragmentListener? = null

    var lineItems: List<SelectableLineItem> = emptyList()
    var returnReasons: List<String> = mockReasons

    init {
        view.setPresenter(this)
        lineItems = lineItemsFromCompoundExtractions(compoundExtractions).map { SelectableLineItem(lineItem = it) }
    }

    override fun selectLineItem(lineItem: SelectableLineItem) {
        lineItem.selected = true
        lineItem.reason = null
        updateView(lineItems)
    }

    override fun deselectLineItem(lineItem: SelectableLineItem) {
        view.showReturnReasonDialog(returnReasons) { selectedReason ->
            if (selectedReason != null) {
                lineItem.selected = false
                lineItem.reason = selectedReason
                updateView(lineItems)
            } else {
                lineItem.selected = true
                lineItem.reason = null
                updateView(lineItems)
            }
        }
    }

    override fun editLineItem(lineItem: SelectableLineItem) {
        listener?.onEditLineItem(lineItem)
    }

    override fun userFeedbackReceived(helpful: Boolean) {
        // TODO
    }

    override fun pay() {
        val (selected, deselected) = lineItems.groupBy { it.selected }.run {
            Pair(get(true)?.map { it.lineItem } ?: emptyList(), get(false)?.map { it.lineItem } ?: emptyList())
        }
        val totalPrice =
                LineItem.createRawAmount(selectedLineItemsTotalAmountSum(lineItems),
                        lineItems.firstOrNull()?.lineItem?.rawCurrency ?: "EUR")
        val (reviewedCompoundExtractions, reviewedExtractions) = updateExtractionsWithReviewedLineItems(compoundExtractions, extractions,
                selected, totalPrice)
        listener?.onPayInvoice(
                selectedLineItems = selected,
                selectedLineItemsTotalPrice = totalPrice,
                deselectedLineItems = deselected,
                reviewedCompoundExtractions = reviewedCompoundExtractions,
                reviewedExtractions = reviewedExtractions)
    }

    override fun updateLineItem(selectableLineItem: SelectableLineItem) {
        lineItems = lineItems.map { sli -> if (sli.lineItem.id == selectableLineItem.lineItem.id) selectableLineItem else sli }
        updateView(lineItems)
    }

    override fun start() {
        updateView(lineItems)
    }

    override fun stop() {
        // TODO
    }

    @VisibleForTesting
    internal fun updateView(lineItems: List<SelectableLineItem>) {
        view.apply {
            showLineItems(lineItems)
            selectedAndTotalLineItemsCount(lineItems).let { (selected, total) ->
                showSelectedAndTotalLineItems(selected, total)
                if (selected > 0) {
                    enablePayButton(selected, total)
                } else {
                    disablePayButton(0, total)
                }
            }
            lineItemsSumIntegralAndFractionalParts(lineItems).let { (integral, fractional) ->
                showSelectedLineItemsSum(integral, fractional)
            }
        }
    }

    private fun selectedAndTotalLineItemsCount(
            lineItems: List<SelectableLineItem>): Pair<Int, Int> =
            Pair(selectedLineItemsCount(lineItems), totalLineItemsCount(lineItems))

    private fun selectedLineItemsCount(lineItems: List<SelectableLineItem>): Int =
            lineItems.fold(0) { c, sli -> if (sli.selected) c + sli.lineItem.quantity else c }

    private fun totalLineItemsCount(lineItems: List<SelectableLineItem>): Int =
            lineItems.fold(0) { c, sli -> c + sli.lineItem.quantity }
}

fun lineItemsFromCompoundExtractions(compoundExtractions: Map<String, GiniVisionCompoundExtraction>): List<LineItem> =
        compoundExtractions["lineItems"]?.run {
            specificExtractionMaps.mapIndexed { index, lineItem ->
                LineItem(index.toString(),
                        lineItem["description"]?.value ?: "",
                        lineItem["quantity"]?.value?.toInt() ?: 0,
                        lineItem["grossPrice"]?.value ?: "")
            }
        } ?: emptyList()

fun updateExtractionsWithReviewedLineItems(compoundExtractions: Map<String, GiniVisionCompoundExtraction>,
                                           extractions: Map<String, GiniVisionSpecificExtraction>,
                                           selectedlineItems: List<LineItem>,
                                           totalPrice: String) =
        Pair(updateCompoundExtractions(compoundExtractions, selectedlineItems), updateExtractions(extractions, totalPrice))

fun updateCompoundExtractions(compoundExtractions: Map<String, GiniVisionCompoundExtraction>,
                              selectedlineItems: List<LineItem>) =
        compoundExtractions.mapValues { (name, extraction) ->
            when (name) {
                "lineItems" -> GiniVisionCompoundExtraction(name,
                        extraction.specificExtractionMaps.mapIndexed { index, lineItemExtractions ->
                            selectedlineItems.find { it.id.toInt() == index }?.let { lineItem ->
                                lineItemExtractions.mapValues { (name, lineItemExtraction) ->
                                    when (name) {
                                        "description" -> copyGiniVisionSpecificExtraction(lineItemExtraction, lineItem.description)
                                        "grossPrice" -> copyGiniVisionSpecificExtraction(lineItemExtraction, lineItem.rawAmount)
                                        "quantity" -> copyGiniVisionSpecificExtraction(lineItemExtraction, lineItem.quantity.toString())
                                        else -> lineItemExtraction
                                    }
                                }
                            }
                        }.filterNotNull())
                else -> extraction
            }
        }

fun updateExtractions(extractions: Map<String, GiniVisionSpecificExtraction>,
                      totalPrice: String) =
        extractions.mapValues { (name, extraction) ->
            when (name) {
                "amountToPay" -> copyGiniVisionSpecificExtraction(extraction, totalPrice)
                else -> extraction
            }
        }

@JvmSynthetic
fun copyGiniVisionSpecificExtraction(other: GiniVisionSpecificExtraction, value: String) =
        GiniVisionSpecificExtraction(other.name, value, other.entity, other.box, other.candidates)