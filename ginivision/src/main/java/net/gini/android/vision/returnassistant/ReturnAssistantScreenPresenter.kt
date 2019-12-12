package net.gini.android.vision.returnassistant

import android.app.Activity
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

internal class ReturnAssistantScreenPresenter(activity: Activity,
                                              view: ReturnAssistantScreenContract.View) :
        ReturnAssistantScreenContract.Presenter(activity, view) {

    override var listener: ReturnAssistantFragmentListener? = null

    init {
        view.setPresenter(this)
    }

    override fun selectLineItem(lineItem: SelectableLineItem) {
        lineItem.selected = true
        view.showLineItems(mockLineItems)
    }

    override fun deselectLineItem(lineItem: SelectableLineItem) {
        lineItem.selected = false
        view.showLineItems(mockLineItems)
    }

    override fun start() {
        view.showLineItems(mockLineItems)
    }

    override fun stop() {
        // TODO
    }


}