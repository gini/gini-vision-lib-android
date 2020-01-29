package net.gini.android.vision.returnassistant

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.gv_fragment_return_assistant.*
import net.gini.android.vision.R

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

private const val TAG_RETURN_REASON_DIALOG = "TAG_RETURN_REASON_DIALOG"

class ReturnAssistantFragment : Fragment(), ReturnAssistantScreenContract.View,
        ReturnAssistantFragmentInterface, LineItemsAdapterListener {

    override var listener: ReturnAssistantFragmentListener?
        get() = this.presenter?.listener
        set(value) {
            this.presenter?.listener = value
        }

    private var presenter: ReturnAssistantScreenContract.Presenter? = null

    companion object {
        @JvmStatic
        fun createInstance() = ReturnAssistantFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this.activity
        checkNotNull(activity) {
            "Missing activity for fragment."
        }
        createPresenter(activity)
        initListener()
    }

    private fun createPresenter(activity: Activity) = ReturnAssistantScreenPresenter(activity, this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(
            R.layout.gv_fragment_return_assistant, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    private fun initListener() {
        if (activity is ReturnAssistantFragmentListener) {
            listener = activity as ReturnAssistantFragmentListener?
        } else checkNotNull(listener) {
            ("MultiPageReviewFragmentListener not set. "
                    + "You can set it with MultiPageReviewFragment#setListener() or "
                    + "by making the host activity implement the MultiPageReviewFragmentListener.")
        }
    }

    private fun initRecyclerView() {
        activity?.let {
            gv_line_items.apply {
                layoutManager = LinearLayoutManager(it)
                adapter = LineItemsAdapter(it, this@ReturnAssistantFragment)
            }
        }
    }

    override fun showLineItems(lineItems: List<SelectableLineItem>) {
        (gv_line_items.adapter as LineItemsAdapter?)?.lineItems = lineItems
    }

    override fun showSelectedAndTotalLineItems(selected: Int, total: Int) {
        (gv_line_items.adapter as LineItemsAdapter?)?.selectedAndTotalItems = "${selected}/${total}"
    }

    override fun enablePayButton(selected: Int, total: Int) {
        gv_pay_button.isEnabled = true
        updatePayButtonTitle(selected, total)
    }

    override fun disablePayButton(selected: Int, total: Int) {
        gv_pay_button.isEnabled = false
        updatePayButtonTitle(selected, total)
    }

    override fun showSelectedLineItemsSum(integralPart: String, fractionPart: String) {
        (gv_line_items.adapter as LineItemsAdapter?)?.totalAmountIntegralAndFractionParts =
                Pair(integralPart, fractionPart)
    }

    override fun showReturnReasonDialog(reasons: List<String>,
                                        resultCallback: DialogResultCallback) {
        ReturnReasonDialog.createInstance(reasons).also {
            it.callback = resultCallback
            it.show(fragmentManager, TAG_RETURN_REASON_DIALOG)
        }
    }

    private fun updatePayButtonTitle(selected: Int, total: Int) {
        @SuppressLint("SetTextI18n")
        gv_pay_button.text = "Pay ${selected}/${total}"
    }

    override fun setPresenter(presenter: ReturnAssistantScreenContract.Presenter) {
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

    override fun onLineItemClicked(lineItem: SelectableLineItem) {
        presenter?.editLineItem(lineItem)
    }

    override fun onLineItemSelected(lineItem: SelectableLineItem) {
        presenter?.selectLineItem(lineItem)
    }

    override fun onLineItemDeselected(lineItem: SelectableLineItem) {
        presenter?.deselectLineItem(lineItem)
    }

    override fun updateLineItem(selectableLineItem: SelectableLineItem) {
        presenter?.updateLineItem(selectableLineItem)
    }

}
