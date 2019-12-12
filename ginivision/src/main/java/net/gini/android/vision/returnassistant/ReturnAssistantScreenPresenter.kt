package net.gini.android.vision.returnassistant

import android.app.Activity
import android.support.annotation.VisibleForTesting
import kotlin.random.Random

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val mockLineItems = List(10) { i ->
    LineItem(id = "$i",
            description = "Nike Sportswear Air Max ${Random.nextInt(1, 99)} - Sneaker Low",
            quantity = 2,
            rawAmount = "${Random.nextInt(25)}.${Random.nextInt(9)}${Random.nextInt(9)}:EUR")
}.map { SelectableLineItem(lineItem = it) }

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
            selectedAndTotalLineItems(lineItems).let { (selected, total) ->
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

    private fun selectedAndTotalLineItems(lineItems: List<SelectableLineItem>): Pair<Int, Int> =
            Pair(lineItems.count { it.selected }, lineItems.size)
}