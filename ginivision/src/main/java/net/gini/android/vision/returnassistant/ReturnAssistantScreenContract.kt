package net.gini.android.vision.returnassistant

import android.app.Activity
import android.content.Intent
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
        fun startActivityForResult(intent: Intent, requestCode: Int)
    }

    abstract class Presenter(activity: Activity, view: View) :
            GiniVisionBasePresenter<View>(activity, view), ReturnAssistantFragmentInterface {

        abstract fun selectLineItem(lineItem: SelectableLineItem)
        abstract fun deselectLineItem(lineItem: SelectableLineItem)
        abstract fun editLineItem(lineItem: SelectableLineItem)
        abstract fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    }
}