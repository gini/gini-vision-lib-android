package net.gini.android.vision.digitalinvoice

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.gv_fragment_what_is_this_dialog.*
import net.gini.android.vision.R

/**
 * Created by Alpar Szotyori on 24.01.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

typealias WhatIsThisDialogResultCallback = (Boolean?) -> Unit

class WhatIsThisDialog : BottomSheetDialogFragment() {

    var callback: WhatIsThisDialogResultCallback? = null

    override fun getTheme(): Int = R.style.GiniVisionTheme_BottomSheetDialog

    companion object {
        @JvmStatic
        fun createInstance() = WhatIsThisDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(
            R.layout.gv_fragment_what_is_this_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListView()
    }

    private fun initListView() {
        activity?.let {
            val responses = listOf(
                    resources.getString(R.string.gv_digital_invoice_what_is_this_dialog_positive_response),
                    resources.getString(R.string.gv_digital_invoice_what_is_this_dialog_negative_response)
            )
            gv_digital_invoice_what_is_this_dialog_responses.adapter =
                    ArrayAdapter<String>(it, R.layout.gv_item_digital_invoice_what_is_this_dialog_response, responses)
            gv_digital_invoice_what_is_this_dialog_responses.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                callback?.invoke(position == 0)
                dismissAllowingStateLoss()
            }
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        callback?.invoke(null)
    }
}