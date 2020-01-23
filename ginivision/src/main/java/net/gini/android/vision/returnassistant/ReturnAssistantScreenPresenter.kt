package net.gini.android.vision.returnassistant

import android.app.Activity
import android.support.annotation.VisibleForTesting
import kotlin.random.Random

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val mockLineItems = List(5) { i ->
    LineItem(id = "$i",
            description = "Nike Sportswear Air Max ${Random.nextInt(1, 50)} - Sneaker Low",
            quantity = Random.nextInt(1, 5),
            rawAmount = "${Random.nextInt(50)}.${Random.nextInt(9)}${Random.nextInt(9)}:EUR")
}.map { SelectableLineItem(lineItem = it) }

private val mockReasons = listOf(
        "Looks different than site image",
        "Poor quality/fault",
        "Doesn't fit properly",
        "Doesn't suite me",
        "Received wrong item",
        "Parcel damaged",
        "Arrived too late"
)

internal open class ReturnAssistantScreenPresenter(activity: Activity,
                                                   view: ReturnAssistantScreenContract.View) :
        ReturnAssistantScreenContract.Presenter(activity, view) {

    override var listener: ReturnAssistantFragmentListener? = null

    var lineItems: List<SelectableLineItem> = emptyList()
    var returnReasons: List<String> = mockReasons

    init {
        view.setPresenter(this)
        lineItems = mockLineItems
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

    override fun updateLineItem(selectableLineItem: SelectableLineItem) {
        lineItems =
                lineItems.map { sli -> if (sli.lineItem.id == selectableLineItem.lineItem.id) selectableLineItem else sli }
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
            lineItemsSumIntegralAndFractionParts(lineItems).let { (integral, fraction) ->
                showSelectedLineItemsSum(integral, fraction)
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