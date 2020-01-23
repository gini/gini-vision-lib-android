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
        fun showSelectedAndTotalLineItems(selected: Int, total: Int)
        fun enablePayButton(selected: Int, total: Int)
        fun disablePayButton(selected: Int, total: Int)
        fun showSelectedLineItemsSum(integralPart: String, fractionPart: String)
    }

    abstract class Presenter(activity: Activity, view: View) :
            GiniVisionBasePresenter<View>(activity, view), ReturnAssistantFragmentInterface {

        abstract fun selectLineItem(lineItem: SelectableLineItem)
        abstract fun deselectLineItem(lineItem: SelectableLineItem)
        abstract fun editLineItem(lineItem: SelectableLineItem)
    }
}