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

fun lineItemsSumIntegralAndFractionalParts(
        lineItems: List<SelectableLineItem>): Pair<String, String> {
    val sum = selectedLineItemsTotalAmountSum(lineItems)
    val currency = lineItemsCurency(lineItems)
    return Pair(amountIntegralPartWithCurrencySymbol(sum, currency),
            sum.fractionalPart(FRACTION_FORMAT))
}

fun lineItemTotalAmountIntegralAndFractionalParts(lineItem: LineItem): Pair<String, String> {
    return lineItem.run {
        Pair(amountIntegralPartWithCurrencySymbol(totalAmount, currency),
                totalAmount.fractionalPart(FRACTION_FORMAT))
    }
}

fun amountIntegralPartWithCurrencySymbol(amount: BigDecimal, currency: Currency?) =
        currency?.let { c ->
            amount.integralPartWithCurrency(c, INTEGRAL_FORMAT)
        } ?: amount.integralPart(INTEGRAL_FORMAT)

fun lineItemsCurency(lineItems: List<SelectableLineItem>): Currency? =
        lineItems.firstOrNull()?.lineItem?.currency

fun selectedLineItemsTotalAmountSum(lineItems: List<SelectableLineItem>): BigDecimal =
        lineItems.fold<SelectableLineItem, BigDecimal>(BigDecimal.ZERO) { sum, sli ->
            if (sli.selected) sum.add(sli.lineItem.totalAmount) else sum
        }