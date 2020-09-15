package net.gini.android.vision.digitalinvoice

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.gv_fragment_return_reason_dialog.*
import net.gini.android.vision.R
import net.gini.android.vision.network.model.GiniVisionReturnReason
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Alpar Szotyori on 22.01.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

private const val ARG_RETURN_REASONS = "GV_ARG_SELECTABLE_LINE_ITEM"

internal typealias ReturnReasonDialogResultCallback = (GiniVisionReturnReason?) -> Unit

/**
 * Internal use only.
 *
 * @suppress
 */
internal class ReturnReasonDialog : BottomSheetDialogFragment() {

    private lateinit var reasons: List<GiniVisionReturnReason>

    var callback: ReturnReasonDialogResultCallback? = null

    override fun getTheme(): Int = R.style.GiniVisionTheme_BottomSheetDialog

    companion object {
        @JvmStatic
        fun createInstance(reasons: List<GiniVisionReturnReason>) = ReturnReasonDialog().apply {
            arguments = Bundle().apply {
                putParcelableArrayList(ARG_RETURN_REASONS, ArrayList(reasons))
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
        arguments?.run {
            reasons = getParcelableArrayList(ARG_RETURN_REASONS) ?: emptyList()
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
                    ArrayAdapter<String>(it, R.layout.gv_item_return_reason, localizedReasons())
            gv_return_reasons_list.onItemClickListener =
                    AdapterView.OnItemClickListener { _, _, position, _ ->
                        callback?.invoke(reasons[position])
                        dismissAllowingStateLoss()
                    }
        }
    }

    private fun localizedReasons() = reasons.map { it.labelInLocalLanguageOrGerman }

    override fun onCancel(dialog: DialogInterface) {
        callback?.invoke(null)
    }
}