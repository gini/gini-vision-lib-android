package net.gini.android.vision.returnassistant

import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.gv_fragment_return_reason_dialog.*
import net.gini.android.vision.R

/**
 * Created by Alpar Szotyori on 22.01.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

private const val ARG_RETURN_REASONS = "GV_ARG_SELECTABLE_LINE_ITEM"

typealias ReturnReasonDialogResultCallback = (String?) -> Unit

class ReturnReasonDialog : BottomSheetDialogFragment() {

    private lateinit var reasons: List<String>

    var callback: ReturnReasonDialogResultCallback? = null

    override fun getTheme(): Int = R.style.GiniVisionTheme_BottomSheetDialog

    companion object {
        @JvmStatic
        fun createInstance(reasons: List<String>) = ReturnReasonDialog().apply {
            arguments = Bundle().apply {
                putStringArrayList(ARG_RETURN_REASONS, ArrayList(reasons))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        readArguments()
    }

    override fun onDestroy() {
        super.onDestroy()
        callback = null
    }

    private fun readArguments() {
        arguments?.let {
            reasons = it.getStringArrayList(ARG_RETURN_REASONS)?.toList() ?: emptyList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(
            R.layout.gv_fragment_return_reason_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListView()
    }

    private fun initListView() {
        activity?.let {
            gv_return_reasons_list.adapter =
                    ArrayAdapter<String>(it, R.layout.gv_item_return_reason, reasons)
            gv_return_reasons_list.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        callback?.invoke(reasons[position])
                        dismissAllowingStateLoss()
                    }
        }
    }

    override fun onCancel(dialog: DialogInterface?) {
        callback?.invoke(null)
    }
}