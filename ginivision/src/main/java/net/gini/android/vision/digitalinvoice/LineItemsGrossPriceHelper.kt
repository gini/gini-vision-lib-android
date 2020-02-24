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

fun lineItemsTotalGrossPriceSumIntegralAndFractionalParts(
        lineItems: List<SelectableLineItem>): Pair<String, String> {
    val sum = selectedLineItemsTotalGrossPriceSum(lineItems)
    val currency = lineItemsCurency(lineItems)
    return Pair(grossPriceIntegralPartWithCurrencySymbol(sum, currency),
            sum.fractionalPart(FRACTION_FORMAT))
}

fun lineItemTotalGrossPriceIntegralAndFractionalParts(lineItem: LineItem): Pair<String, String> {
    return lineItem.run {
        Pair(grossPriceIntegralPartWithCurrencySymbol(totalGrossPrice, currency),
                totalGrossPrice.fractionalPart(FRACTION_FORMAT))
    }
}

fun grossPriceIntegralPartWithCurrencySymbol(grossPrice: BigDecimal, currency: Currency?) =
        currency?.let { c ->
            grossPrice.integralPartWithCurrency(c, INTEGRAL_FORMAT)
        } ?: grossPrice.integralPart(INTEGRAL_FORMAT)

fun lineItemsCurency(lineItems: List<SelectableLineItem>): Currency? =
        lineItems.firstOrNull()?.lineItem?.currency

fun selectedLineItemsTotalGrossPriceSum(lineItems: List<SelectableLineItem>): BigDecimal =
        lineItems.fold<SelectableLineItem, BigDecimal>(BigDecimal.ZERO) { sum, sli ->
            if (sli.selected) sum.add(sli.lineItem.totalGrossPrice) else sum
        }