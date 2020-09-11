package net.gini.android.vision.digitalinvoice.details

import android.app.Activity
import net.gini.android.vision.digitalinvoice.DigitalInvoice
import net.gini.android.vision.digitalinvoice.SelectableLineItem
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsScreenContract.Presenter
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsScreenContract.View
import net.gini.android.vision.digitalinvoice.toPriceString
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.ParseException

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@JvmSynthetic
internal val GROSS_PRICE_FORMAT = DecimalFormat("#,##0.00").apply { isParseBigDecimal = true }

/**
 * Internal use only.
 *
 * @suppress
 */
internal class LineItemDetailsScreenPresenter(activity: Activity, view: View,
                                     var selectableLineItem: SelectableLineItem,
                                     private val grossPriceFormat: DecimalFormat = GROSS_PRICE_FORMAT) :
        Presenter(activity, view) {

    override var listener: LineItemDetailsFragmentListener? = null

    var returnReasons: List<String> = emptyList()

    private val originalLineItem: SelectableLineItem = selectableLineItem.copy()

    init {
        view.setPresenter(this)
    }

    override fun selectLineItem() {
        if (selectableLineItem.selected) {
            return
        }
        selectableLineItem.selected = true
        selectableLineItem.reason = null
        view.apply {
            enableInput()
            selectableLineItem.run {
                showCheckbox(selected, lineItem.quantity)
                updateSaveButton(this, originalLineItem)
            }
        }
    }

    override fun deselectLineItem() {
        if (!selectableLineItem.selected) {
            return
        }
        if (returnReasons.isEmpty()) {
            selectableLineItem.selected = false
            selectableLineItem.reason = null
            view.disableInput()
        } else {
            view.showReturnReasonDialog(returnReasons) { selectedReason ->
                if (selectedReason != null) {
                    selectableLineItem.selected = false
                    selectableLineItem.reason = selectedReason
                    view.disableInput()
                } else {
                    selectableLineItem.selected = true
                    selectableLineItem.reason = null
                    view.enableInput()
                }
            }
        }
        selectableLineItem.let {
            view.apply {
                showCheckbox(it.selected, it.lineItem.quantity)
                updateSaveButton(it, originalLineItem)
            }
        }
    }

    override fun setDescription(description: String) {
        if (selectableLineItem.lineItem.description == description) {
            return
        }
        selectableLineItem = selectableLineItem.copy(
                lineItem = selectableLineItem.lineItem.copy(description = description)
        ).also {
            view.updateSaveButton(it, originalLineItem)
        }
    }

    override fun setQuantity(quantity: Int) {
        if (selectableLineItem.lineItem.quantity == quantity) {
            return
        }
        selectableLineItem = selectableLineItem.copy(
                lineItem = selectableLineItem.lineItem.copy(quantity = quantity)
        ).also {
            view.apply {
                showTotalGrossPrice(it)
                showCheckbox(it.selected, it.lineItem.quantity)
                updateSaveButton(it, originalLineItem)
            }
        }
    }

    override fun setGrossPrice(displayedGrossPrice: String) {
        val grossPrice = try {
            grossPriceFormat.parse(displayedGrossPrice) as BigDecimal
        } catch (_: ParseException) {
            return
        }
        if (selectableLineItem.lineItem.grossPrice == grossPrice) {
            return
        }
        selectableLineItem = selectableLineItem.copy(
                lineItem = selectableLineItem.lineItem.copy(
                        rawGrossPrice = grossPrice.toPriceString(selectableLineItem.lineItem.rawCurrency)
                )
        ).also {
            view.apply {
                showTotalGrossPrice(it)
                updateSaveButton(it, originalLineItem)
            }
        }
    }

    override fun save() {
        listener?.onSave(selectableLineItem)
    }

    override fun start() {
        view.apply {
            selectableLineItem.run {
                showCheckbox(selected, lineItem.quantity)
                lineItem.run {
                    showDescription(description)
                    showQuantity(quantity)
                    showGrossPrice(grossPriceFormat.format(grossPrice), currency?.symbol ?: "")
                }
                showTotalGrossPrice(this)
                updateSaveButton(this, originalLineItem)
            }
        }
    }

    override fun stop() {
    }
}

private fun View.showTotalGrossPrice(selectableLineItem: SelectableLineItem) {
    DigitalInvoice.lineItemTotalGrossPriceIntegralAndFractionalParts(
            selectableLineItem.lineItem).let { (integral, fractional) ->
        showTotalGrossPrice(integral, fractional)
    }
}

private fun View.updateSaveButton(new: SelectableLineItem, old: SelectableLineItem) {
    if (new == old) {
        disableSaveButton()
    } else {
        enableSaveButton()
    }
}