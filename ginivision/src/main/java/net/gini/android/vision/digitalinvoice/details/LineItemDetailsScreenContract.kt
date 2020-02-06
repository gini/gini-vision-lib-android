package net.gini.android.vision.digitalinvoice.details

import android.app.Activity
import net.gini.android.vision.GiniVisionBasePresenter
import net.gini.android.vision.GiniVisionBaseView
import net.gini.android.vision.digitalinvoice.ReturnReasonDialogResultCallback

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface LineItemDetailsScreenContract {

    interface View : GiniVisionBaseView<Presenter> {
        fun showDescription(description: String)
        fun showQuantity(quantity: Int)
        fun showAmount(displayedAmount: String, currency: String)
        fun showCheckbox(selected: Boolean, quantity: Int)
        fun showTotalAmount(integralPart: String, fractionalPart: String)
        fun enableSaveButton()
        fun disableSaveButton()
        fun enableInput()
        fun disableInput()
        fun showReturnReasonDialog(reasons: List<String>,
                                   resultCallback: ReturnReasonDialogResultCallback)
    }

    abstract class Presenter(activity: Activity, view: View) :
            GiniVisionBasePresenter<View>(activity, view), LineItemDetailsFragmentInterface {

        abstract fun selectLineItem()
        abstract fun deselectLineItem()
        abstract fun setDescription(description: String)
        abstract fun setQuantity(quantity: Int)
        abstract fun setAmount(displayedAmount: String)
        abstract fun save()
    }
}
