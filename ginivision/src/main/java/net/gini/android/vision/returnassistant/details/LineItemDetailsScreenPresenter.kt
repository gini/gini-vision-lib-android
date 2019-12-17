package net.gini.android.vision.returnassistant.details

import android.app.Activity
import net.gini.android.vision.returnassistant.SelectableLineItem
import net.gini.android.vision.returnassistant.details.LineItemDetailsScreenContract.Presenter
import net.gini.android.vision.returnassistant.details.LineItemDetailsScreenContract.View
import java.math.BigDecimal

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class LineItemDetailsScreenPresenter(activity: Activity, view: View,
                                     var lineItem: SelectableLineItem) : Presenter(activity, view) {

    override var listener: LineItemDetailsFragmentListener? = null

    init {
        view.setPresenter(this)
    }

    override fun selectLineItem() {
        // TODO
    }

    override fun deselectLineItem() {
        // TODO
    }

    override fun setDescription(description: String) {
        // TODO
    }

    override fun setQuantity(quantity: Int) {
        // TODO
    }

    override fun setAmount(amount: BigDecimal) {
        // TODO
    }

    override fun start() {
        view.showLineItem(lineItem)
    }

    override fun stop() {
        // TODO
    }
}