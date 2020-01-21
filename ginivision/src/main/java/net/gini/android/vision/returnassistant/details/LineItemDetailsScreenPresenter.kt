package net.gini.android.vision.returnassistant.details

import android.app.Activity
import net.gini.android.vision.returnassistant.LineItem
import net.gini.android.vision.returnassistant.SelectableLineItem
import net.gini.android.vision.returnassistant.details.LineItemDetailsScreenContract.Presenter
import net.gini.android.vision.returnassistant.details.LineItemDetailsScreenContract.View
import net.gini.android.vision.returnassistant.lineItemTotalAmountIntegralAndFractionParts
import java.math.BigDecimal

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class LineItemDetailsScreenPresenter(activity: Activity, view: View,
                                     var selectableLineItem: SelectableLineItem) :
        Presenter(activity, view) {

    override var listener: LineItemDetailsFragmentListener? = null

    private val originalLineItem: SelectableLineItem = selectableLineItem.copy()

    init {
        view.setPresenter(this)
    }

    override fun selectLineItem() {
        if (selectableLineItem.selected) {
            return
        }
        selectableLineItem.selected = true
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
        selectableLineItem.selected = false
        view.apply {
            disableInput()
            selectableLineItem.run {
                showCheckbox(selected, lineItem.quantity)
                updateSaveButton(this, originalLineItem)
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
                showTotalAmount(it)
                showCheckbox(it.selected, it.lineItem.quantity)
                updateSaveButton(it, originalLineItem)
            }
        }
    }

    override fun setAmount(amount: BigDecimal) {
        if (selectableLineItem.lineItem.amount == amount) {
            return
        }
        selectableLineItem = selectableLineItem.copy(
                lineItem = selectableLineItem.lineItem.copy(
                        rawAmount = LineItem.createRawAmount(amount.toString(),
                                selectableLineItem.lineItem.rawCurrency)
                )
        ).also {
            view.apply {
                showTotalAmount(it)
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
                    showAmount(amount)
                }
                showTotalAmount(this)
                updateSaveButton(this, originalLineItem)
            }
        }
    }

    override fun stop() {
        // TODO
    }
}

private fun View.showTotalAmount(selectableLineItem: SelectableLineItem) {
    lineItemTotalAmountIntegralAndFractionParts(
            selectableLineItem.lineItem).let { (integral, fraction) ->
        showTotalAmount(integral, fraction)
    }
}

private fun View.updateSaveButton(new: SelectableLineItem, old: SelectableLineItem) {
    if (new == old) {
        disableSaveButton()
    } else {
        enableSaveButton()
    }
}