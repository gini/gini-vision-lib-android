package net.gini.android.vision.returnassistant

import android.app.Activity
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.random.Random

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val INTEGRAL_FORMAT = DecimalFormat("#")
val FRACTION_FORMAT = DecimalFormat(".00")

val mockLineItems = List(10) { i ->
    LineItem(id = "$i",
            description = "Nike Sportswear Air Max ${Random.nextInt(1, 99)} - Sneaker Low",
            quantity = 2,
            rawAmount = "${Random.nextInt(25)}.${Random.nextInt(9)}${Random.nextInt(9)}:EUR")
}.map { SelectableLineItem(lineItem = it) }

internal class ReturnAssistantScreenPresenter(activity: Activity,
                                              view: ReturnAssistantScreenContract.View) :
        ReturnAssistantScreenContract.Presenter(activity, view) {

    override var listener: ReturnAssistantFragmentListener? = null

    init {
        view.setPresenter(this)
    }

    override fun selectLineItem(lineItem: SelectableLineItem) {
        lineItem.selected = true
        updateView(mockLineItems)
    }

    override fun deselectLineItem(lineItem: SelectableLineItem) {
        lineItem.selected = false
        updateView(mockLineItems)
    }

    override fun start() {
        updateView(mockLineItems)
    }

    override fun stop() {
        // TODO
    }

    private fun updateView(lineItems: List<SelectableLineItem>) {
        view.apply {
            showLineItems(lineItems)
            val (selected, total) = selectedAndTotalLineItems(lineItems)
            showSelectedAndTotalLineItems(selected, total)
            if (selected > 0) {
                enablePayButton(selected, total)
            } else {
                disablePayButton(0, total)
            }
            val (integral, fraction) = lineItemsSumIntegralAndFractionParts(lineItems)
            showSelectedLineItemsSum(integral, fraction)
        }
    }

    private fun lineItemsSumIntegralAndFractionParts(
            lineItems: List<SelectableLineItem>): Pair<String, String> {
        val sum = lineItemsAmountSum(lineItems)
        val currency = lineItemsCurency(lineItems)
        return Pair(amountIntegralPartWithCurrencySymbol(sum, currency),
                sum.fractionPart(FRACTION_FORMAT))
    }

    private fun amountIntegralPartWithCurrencySymbol(amount: BigDecimal, currency: Currency?) =
            currency?.let { c ->
                amount.integralPartWithCurrency(c, INTEGRAL_FORMAT)
            } ?: amount.integralPart(INTEGRAL_FORMAT)

    private fun lineItemsCurency(lineItems: List<SelectableLineItem>): Currency? =
            if (lineItems.isEmpty()) null else lineItems.first().lineItem.currency

    private fun lineItemsAmountSum(lineItems: List<SelectableLineItem>) =
            if (lineItems.isEmpty()) {
                BigDecimal.ZERO
            } else {
                lineItems.fold<SelectableLineItem, BigDecimal>(
                        BigDecimal.ZERO) { sum, sli ->
                    if (sli.selected) sum.add(sli.lineItem.amount) else sum
                }
            }

    private fun selectedAndTotalLineItems(lineItems: List<SelectableLineItem>): Pair<Int, Int> =
            Pair(lineItems.count { it.selected }, lineItems.size)
}