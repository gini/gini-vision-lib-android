package net.gini.android.vision.digitalinvoice

import android.app.Activity
import net.gini.android.vision.GiniVisionBasePresenter
import net.gini.android.vision.GiniVisionBaseView

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
interface DigitalInvoiceScreenContract {

    /**
     * Internal use only.
     *
     * @suppress
     */
    interface View : GiniVisionBaseView<Presenter> {
        fun showLineItems(lineItems: List<SelectableLineItem>)
        fun showSelectedAndTotalLineItems(selected: Int, total: Int)
        fun showAddons(addons: List<DigitalInvoiceAddon>)
        fun enablePayButton(selected: Int, total: Int)
        fun disablePayButton(selected: Int, total: Int)
        fun showSelectedLineItemsSum(integralPart: String, fractionalPart: String)
        fun showReturnReasonDialog(reasons: List<String>,
                                   resultCallback: ReturnReasonDialogResultCallback)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    abstract class Presenter(activity: Activity, view: View) :
            GiniVisionBasePresenter<View>(activity, view), DigitalInvoiceFragmentInterface {

        abstract fun selectLineItem(lineItem: SelectableLineItem)
        abstract fun deselectLineItem(lineItem: SelectableLineItem)
        abstract fun editLineItem(lineItem: SelectableLineItem)
        abstract fun userFeedbackReceived(helpful: Boolean)
        abstract fun pay()
    }
}