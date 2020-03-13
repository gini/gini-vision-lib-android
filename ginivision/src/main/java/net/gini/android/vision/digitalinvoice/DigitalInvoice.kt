package net.gini.android.vision.digitalinvoice

import android.support.annotation.VisibleForTesting
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Alpar Szotyori on 11.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

val INTEGRAL_FORMAT = DecimalFormat("#,###")
val FRACTION_FORMAT = DecimalFormat(".00").apply { roundingMode = RoundingMode.DOWN }

class DigitalInvoice(extractions: Map<String, GiniVisionSpecificExtraction>,
                     compoundExtractions: Map<String, GiniVisionCompoundExtraction>) {

    private var _extractions: Map<String, GiniVisionSpecificExtraction> = extractions
    val extractions
        get() = _extractions

    private var _compoundExtractions: Map<String, GiniVisionCompoundExtraction> = compoundExtractions
    val compoundExtractions
        get() = _compoundExtractions

    private var _selectableLineItems: List<SelectableLineItem>
    val selectableLineItems
        get() = _selectableLineItems

    init {
        _selectableLineItems = lineItemsFromCompoundExtractions(compoundExtractions).map { SelectableLineItem(lineItem = it) }
    }

    companion object {
        fun lineItemTotalGrossPriceIntegralAndFractionalParts(lineItem: LineItem): Pair<String, String> {
            return lineItem.run {
                Pair(grossPriceIntegralPartWithCurrencySymbol(totalGrossPrice, currency),
                        totalGrossPrice.fractionalPart(FRACTION_FORMAT))
            }
        }

        @VisibleForTesting
        fun grossPriceIntegralPartWithCurrencySymbol(grossPrice: BigDecimal, currency: Currency?) =
                currency?.let { c ->
                    grossPrice.integralPartWithCurrency(c, INTEGRAL_FORMAT)
                } ?: grossPrice.integralPart(INTEGRAL_FORMAT)
    }

    private fun lineItemsFromCompoundExtractions(compoundExtractions: Map<String, GiniVisionCompoundExtraction>): List<LineItem> =
            compoundExtractions["lineItems"]?.run {
                specificExtractionMaps.mapIndexed { index, lineItem ->
                    LineItem(index.toString(),
                            lineItem["description"]?.value ?: "",
                            lineItem["quantity"]?.value?.toIntOrNull() ?: 0,
                            lineItem["baseGross"]?.value ?: "")
                }
            } ?: emptyList()

    fun updateLineItem(selectableLineItem: SelectableLineItem) {
        _selectableLineItems =
                selectableLineItems.map { sli -> if (sli.lineItem.id == selectableLineItem.lineItem.id) selectableLineItem else sli }
    }

    fun selectLineItem(selectableLineItem: SelectableLineItem) {
        selectableLineItems.find { sli -> sli.lineItem.id == selectableLineItem.lineItem.id }?.let { sli ->
            sli.selected = true
            sli.reason = null
        }
    }

    fun deselectLineItem(selectableLineItem: SelectableLineItem, reason: String) {
        selectableLineItems.find { sli -> sli.lineItem.id == selectableLineItem.lineItem.id }?.let { sli ->
            sli.selected = false
            sli.reason = reason
        }
    }

    fun lineItemsTotalGrossPriceSumIntegralAndFractionalParts(): Pair<String, String> {
        val sum = selectedLineItemsTotalGrossPriceSum()
        val currency = lineItemsCurency()
        return Pair(grossPriceIntegralPartWithCurrencySymbol(sum, currency),
                sum.fractionalPart(FRACTION_FORMAT))
    }

    @VisibleForTesting
    fun lineItemsCurency(): Currency? =
            selectableLineItems.firstOrNull()?.lineItem?.currency

    fun selectedLineItemsTotalGrossPriceSum(): BigDecimal =
            selectableLineItems.fold<SelectableLineItem, BigDecimal>(BigDecimal.ZERO) { sum, sli ->
                if (sli.selected) sum.add(sli.lineItem.totalGrossPrice) else sum
            }

    fun selectedAndTotalLineItemsCount(): Pair<Int, Int> =
            Pair(selectedLineItemsCount(), totalLineItemsCount())

    private fun selectedLineItemsCount(): Int =
            selectableLineItems.fold(0) { c, sli -> if (sli.selected) c + sli.lineItem.quantity else c }

    private fun totalLineItemsCount(): Int =
            selectableLineItems.fold(0) { c, sli -> c + sli.lineItem.quantity }

    fun updateLineItemExtractionsWithReviewedLineItems() {
        _compoundExtractions = compoundExtractions.mapValues { (name, extraction) ->
            when (name) {
                "lineItems" -> GiniVisionCompoundExtraction(name,
                        extraction.specificExtractionMaps.mapIndexed { index, lineItemExtractions ->
                            selectableLineItems.find { it.lineItem.id.toInt() == index }?.let { sli ->
                                lineItemExtractions.mapValues { (name, lineItemExtraction) ->
                                    when (name) {
                                        "description" -> copyGiniVisionSpecificExtraction(lineItemExtraction, sli.lineItem.description)
                                        "baseGross" -> copyGiniVisionSpecificExtraction(lineItemExtraction, sli.lineItem.rawGrossPrice)
                                        "quantity" -> copyGiniVisionSpecificExtraction(lineItemExtraction,
                                                if (sli.selected) {
                                                    sli.lineItem.quantity.toString()
                                                } else {
                                                    "0"
                                                })
                                        else -> lineItemExtraction
                                    }
                                }
                            }
                        }.filterNotNull())
                else -> extraction
            }
        }
    }

    fun updateAmountToPayExtractionWithTotalGrossPrice() {
        val totalPrice = LineItem.createRawGrossPrice(selectedLineItemsTotalGrossPriceSum(),
                selectableLineItems.firstOrNull()?.lineItem?.rawCurrency ?: "EUR")

        _extractions = if (extractions.containsKey("amountToPay")) {
            extractions.mapValues { (name, extraction) ->
                when (name) {
                    "amountToPay" -> copyGiniVisionSpecificExtraction(extraction, totalPrice)
                    else -> extraction
                }
            }
        } else {
            extractions.toMutableMap().apply {
                put("amountToPay", GiniVisionSpecificExtraction("amountToPay", totalPrice, "amount", null, emptyList()))
            }
        }
    }

    @JvmSynthetic
    private fun copyGiniVisionSpecificExtraction(other: GiniVisionSpecificExtraction, value: String) =
            GiniVisionSpecificExtraction(other.name, value, other.entity, other.box, other.candidates)
}