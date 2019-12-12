package net.gini.android.vision.returnassistant

import android.app.Activity
import net.gini.android.vision.GiniVisionBasePresenter
import net.gini.android.vision.GiniVisionBaseView

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface ReturnAssistantScreenContract {

    interface View : GiniVisionBaseView<Presenter> {
        fun showLineItems(lineItems: List<SelectableLineItem>)
    }

    abstract class Presenter(activity: Activity, view: View) :
            GiniVisionBasePresenter<View>(activity, view), ReturnAssistantFragmentInterface {

        abstract fun selectLineItem(lineItem: SelectableLineItem)
        abstract fun deselectLineItem(lineItem: SelectableLineItem)
    }
}

data class SelectableLineItem(var selected: Boolean = true, val lineItem: LineItem)