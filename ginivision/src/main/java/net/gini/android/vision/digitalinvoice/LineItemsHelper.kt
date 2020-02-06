package net.gini.android.vision.digitalinvoice

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Alpar Szotyori on 12.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val INTEGRAL_FORMAT = DecimalFormat("#,###")
val FRACTION_FORMAT = DecimalFormat(".00").apply { roundingMode = RoundingMode.DOWN }

fun lineItemsSumIntegralAndFractionParts(
        lineItems: List<SelectableLineItem>): Pair<String, String> {
    val sum = lineItemsTotalAmountSum(lineItems)
    val currency = lineItemsCurency(lineItems)
    return Pair(amountIntegralPartWithCurrencySymbol(sum, currency),
            sum.fractionPart(FRACTION_FORMAT))
}

fun lineItemTotalAmountIntegralAndFractionParts(lineItem: LineItem): Pair<String, String> {
    return lineItem.run {
        Pair(amountIntegralPartWithCurrencySymbol(totalAmount, currency),
                totalAmount.fractionPart(FRACTION_FORMAT))
    }
}

fun amountIntegralPartWithCurrencySymbol(amount: BigDecimal, currency: Currency?) =
        currency?.let { c ->
            amount.integralPartWithCurrency(c, INTEGRAL_FORMAT)
        } ?: amount.integralPart(INTEGRAL_FORMAT)

fun lineItemsCurency(lineItems: List<SelectableLineItem>): Currency? =
        if (lineItems.isEmpty()) null else lineItems.first().lineItem.currency

fun lineItemsTotalAmountSum(lineItems: List<SelectableLineItem>): BigDecimal =
        if (lineItems.isEmpty()) {
            BigDecimal.ZERO
        } else {
            lineItems.fold<SelectableLineItem, BigDecimal>(
                    BigDecimal.ZERO) { sum, sli ->
                if (sli.selected) sum.add(sli.lineItem.totalAmount) else sum
            }
        }