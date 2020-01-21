package net.gini.android.vision.returnassistant

import android.app.Activity
import android.content.Intent
import android.support.annotation.VisibleForTesting
import net.gini.android.vision.returnassistant.details.LineItemDetailsActivity
import kotlin.random.Random

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val mockLineItems = List(50) { i ->
    LineItem(id = "$i",
            description = "Nike Sportswear Air Max ${Random.nextInt(1, 99)} - Sneaker Low",
            quantity = Random.nextInt(10),
            rawAmount = "${Random.nextInt(2500)}.${Random.nextInt(9)}${Random.nextInt(9)}:EUR")
}.map { SelectableLineItem(lineItem = it) }

private const val EDIT_LINE_ITEM_REQUEST = 1

internal open class ReturnAssistantScreenPresenter(activity: Activity,
                                                   view: ReturnAssistantScreenContract.View) :
        ReturnAssistantScreenContract.Presenter(activity, view) {

    override var listener: ReturnAssistantFragmentListener? = null
    var lineItems: List<SelectableLineItem> = emptyList()

    init {
        view.setPresenter(this)
        lineItems = mockLineItems
    }

    override fun selectLineItem(lineItem: SelectableLineItem) {
        lineItem.selected = true
        updateView(lineItems)
    }

    override fun deselectLineItem(lineItem: SelectableLineItem) {
        lineItem.selected = false
        updateView(lineItems)
    }

    override fun editLineItem(lineItem: SelectableLineItem) {
        view.startActivityForResult(LineItemDetailsActivity.createIntent(activity, lineItem),
                EDIT_LINE_ITEM_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            EDIT_LINE_ITEM_REQUEST -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        data?.getParcelableExtra<SelectableLineItem>(
                                LineItemDetailsActivity.EXTRA_OUT_SELECTABLE_LINE_ITEM)?.let {
                            lineItems =
                                    lineItems.map { sli -> if (sli.lineItem.id == it.lineItem.id) it else sli }
                            updateView(lineItems)
                        }
                    }
                }
            }
        }
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