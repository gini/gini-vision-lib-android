package net.gini.android.vision.returnassistant.details


import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.gini.android.vision.R
import net.gini.android.vision.returnassistant.LineItem
import net.gini.android.vision.returnassistant.SelectableLineItem

private const val ARG_SELECTABLE_LINE_ITEM = "GV_ARG_SELECTABLE_LINE_ITEM"

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class LineItemDetailsFragment : Fragment(), LineItemDetailsScreenContract.View,
        LineItemDetailsFragmentInterface {

    override var listener: LineItemDetailsFragmentListener?
        get() = this.presenter?.listener
        set(value) {
            this.presenter?.listener = value
        }

    private var presenter: LineItemDetailsScreenContract.Presenter? = null

    private lateinit var lineItem: SelectableLineItem

    companion object {
        @JvmStatic
        fun createInstance(
                selectableLineItem: SelectableLineItem) = LineItemDetailsFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_SELECTABLE_LINE_ITEM, selectableLineItem)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this.activity
        checkNotNull(activity) {
            "Missing activity for fragment."
        }
        readArguments()
        createPresenter(activity)
        initListener()
    }

    private fun readArguments() {
        arguments?.let {
            lineItem = it.getParcelable(ARG_SELECTABLE_LINE_ITEM) ?: SelectableLineItem(false,
                    LineItem("", "", 0, ""))
        }
    }

    private fun createPresenter(activity: Activity) = LineItemDetailsScreenPresenter(activity, this,
            lineItem)

    private fun initListener() {
        if (activity is LineItemDetailsFragmentListener) {
            listener = activity as LineItemDetailsFragmentListener?
        } else checkNotNull(listener) {
            ("LineItemDetailsFragmentListener not set. "
                    + "You can set it with LineItemDetailsFragmentListener#setListener() or "
                    + "by making the host activity implement the LineItemDetailsFragmentListener.")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(
            R.layout.gv_fragment_line_item_details, container, false)

    override fun setPresenter(presenter: LineItemDetailsScreenContract.Presenter) {
        this.presenter = presenter
    }

    override fun onStart() {
        super.onStart()
        presenter?.start()
    }

    override fun onStop() {
        super.onStop()
        presenter?.stop()
    }

    override fun showLineItem(lineItem: SelectableLineItem) {
        // TODO
        Log.d("view", "show line item: $lineItem")
    }

}
