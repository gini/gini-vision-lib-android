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
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val INTEGRAL_FORMAT = DecimalFormat("#")
val FRACTION_FORMAT = DecimalFormat(".00")

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
        showSelectedAndTotalLineItems(lineItems)
        showSelectedLineItemsAmountSum(lineItems)
        updatePayButton(lineItems)
    }

    private fun showSelectedAndTotalLineItems(lineItems: List<SelectableLineItem>) {
        @SuppressLint("SetTextI18n")
        gv_total_and_selected_items.text = selectedAndTotalLineItemsString(lineItems)
    }

    private fun selectedAndTotalLineItemsString(lineItems: List<SelectableLineItem>): String =
            "${lineItems.count { it.selected }}/${lineItems.size}"

    private fun updatePayButton(lineItems: List<SelectableLineItem>) {
        gv_pay_button.apply {
            isEnabled = lineItems.any { it.selected }
            text = if (lineItems.isEmpty()) {
                "Pay"
            } else {
                "Pay ${selectedAndTotalLineItemsString(lineItems)}"
            }
        }
    }

    private fun showSelectedLineItemsAmountSum(lineItems: List<SelectableLineItem>) {
        val sum = lineItemsAmountSum(lineItems)
        val currency = lineItemsCurency(lineItems)
        gv_amount_total_integral_part.text = amountIntegralPartWithCurrencySymbol(sum, currency)
        gv_amount_total_fraction_part.text = sum.fractionPart(FRACTION_FORMAT)
    }

    private fun lineItemsCurency(lineItems: List<SelectableLineItem>): Currency? =
            if (lineItems.isEmpty()) null else lineItems.first().lineItem.currency

    private fun lineItemsAmountSum(lineItems: List<SelectableLineItem>) =
            if (lineItems.isEmpty()) {
                BigDecimal.ZERO
            } else {
                lineItems.fold<SelectableLineItem, BigDecimal>(
                        BigDecimal.ZERO) { sum, sli ->
                    if (sli.selected) sum.add(sli.lineItem.amount) else sum
                }
            }

    private fun amountIntegralPartWithCurrencySymbol(amount: BigDecimal, currency: Currency?) =
            currency?.let { c ->
                amount.integralPartWithCurrency(c, INTEGRAL_FORMAT)
            } ?: amount.integralPart(INTEGRAL_FORMAT)

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
        // TODO
    }

    override fun onLineItemSelected(lineItem: SelectableLineItem) {
        presenter?.selectLineItem(lineItem)
    }

    override fun onLineItemDeselected(lineItem: SelectableLineItem) {
        presenter?.deselectLineItem(lineItem)
    }

}
