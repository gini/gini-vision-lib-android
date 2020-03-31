package net.gini.android.vision.digitalinvoice

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.gv_fragment_digital_invoice.*
import net.gini.android.vision.R
import net.gini.android.vision.internal.util.ActivityHelper.forcePortraitOrientationOnPhones
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

private const val ARGS_EXTRACTIONS = "GV_ARGS_EXTRACTIONS"
private const val ARGS_COMPOUND_EXTRACTIONS = "GV_ARGS_COMPOUND_EXTRACTIONS"

private const val TAG_RETURN_REASON_DIALOG = "TAG_RETURN_REASON_DIALOG"
private const val TAG_WHAT_IS_THIS_DIALOG = "TAG_WHAT_IS_THIS_DIALOG"

/**
 * When you use the Component API the `DigitalInvoiceFragment` displays the line items extracted from an invoice document and their total
 * price. The user can deselect line items which should not be paid for and also edit the quantity, price or description of each line item. The
 * total price is always updated to include only the selected line items.
 *
 * The returned extractions in the [DigitalInvoiceFragmentListener.onPayInvoice()] are updated to include the user's midifications:
 * - "amountToPay" is updated to contain the sum of the selected line items' prices,
 * - the line items are updated according to the user's modifications.
 *
 * You should show the `DigitalInvoiceFragment` when the
 * [AnalysisFragmentListener.onProceedToReturnAssistant()] is called.
 *
 * Include the `DigitalInvoiceFragment` into your layout by using the [DigitalInvoiceFragment.createInstance()] factory method to create
 * an instance and display it using the [androidx.fragment.app.FragmentManager].
 *
 * A [DigitalInvoiceFragmentListener] instance must be available before the `DigitalInvoiceFragment` is attached to an Activity. Failing to
 * do so will throw an exception. The listener instance can be provided either implicitly by making the hosting Activity implement the
 * [DigitalInvoiceFragmentListener] interface or explicitly by setting the listener using [DigitalInvoiceFragment.listener].
 *
 * Your Activity is automatically set as the listener in [DigitalInvoiceFragment.onCreate()].
 *
 * ### Customizing the Digital Invoice Screen
 *
 * See the [DigitalInvoiceActivity] for details.
 */
class DigitalInvoiceFragment : Fragment(), DigitalInvoiceScreenContract.View,
        DigitalInvoiceFragmentInterface, LineItemsAdapterListener {

    override var listener: DigitalInvoiceFragmentListener?
        get() = this.presenter?.listener
        set(value) {
            this.presenter?.listener = value
        }

    private var presenter: DigitalInvoiceScreenContract.Presenter? = null

    private var extractions: Map<String, GiniVisionSpecificExtraction> = emptyMap()
    private var compoundExtractions: Map<String, GiniVisionCompoundExtraction> = emptyMap()

    companion object {

        /**
         * Factory method for creating a new instance of the `DigitalInvoiceFragment` using the provided extractions.
         *
         * **Note:** Always use this method to create new instances. The extractions are required and passed as fragment arguments to the
         * instance.
         *
         * @param extractions a map of [GiniVisionSpecificExtraction]s
         * @param compoundExtractions a map of [GiniVisionCompoundExtraction]s
         */
        @JvmStatic
        fun createInstance(extractions: Map<String, GiniVisionSpecificExtraction>,
                           compoundExtractions: Map<String, GiniVisionCompoundExtraction>) = DigitalInvoiceFragment().apply {
            arguments = Bundle().apply {
                putBundle(ARGS_EXTRACTIONS, Bundle().apply {
                    extractions.forEach { putParcelable(it.key, it.value) }
                })
                putBundle(ARGS_COMPOUND_EXTRACTIONS, Bundle().apply {
                    compoundExtractions.forEach { putParcelable(it.key, it.value) }
                })
            }
        }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activity = this.activity
        checkNotNull(activity) {
            "Missing activity for fragment."
        }
        forcePortraitOrientationOnPhones(activity)
        readArguments()
        createPresenter(activity)
        initListener()
    }

    private fun readArguments() {
        arguments?.run {
            getBundle(ARGS_EXTRACTIONS)?.run {
                extractions = keySet().map { it to getParcelable<GiniVisionSpecificExtraction>(it)!! }.toMap()
            }
            getBundle(ARGS_COMPOUND_EXTRACTIONS)?.run {
                compoundExtractions = keySet().map { it to getParcelable<GiniVisionCompoundExtraction>(it)!! }.toMap()
            }
        }
    }

    private fun createPresenter(activity: Activity) = DigitalInvoiceScreenPresenter(activity, this, extractions, compoundExtractions)

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? = inflater.inflate(
            R.layout.gv_fragment_digital_invoice, container, false)

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        setInputHandlers()
    }

    private fun initListener() {
        if (activity is DigitalInvoiceFragmentListener) {
            listener = activity as DigitalInvoiceFragmentListener?
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
                adapter = LineItemsAdapter(it, this@DigitalInvoiceFragment)
            }
        }
    }

    private fun setInputHandlers() {
        gv_pay_button.setOnClickListener {
            presenter?.pay()
        }
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun showLineItems(lineItems: List<SelectableLineItem>) {
        (gv_line_items.adapter as LineItemsAdapter?)?.lineItems = lineItems
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun showSelectedAndTotalLineItems(selected: Int, total: Int) {
        (gv_line_items.adapter as LineItemsAdapter?)?.selectedAndTotalItems = "${selected}/${total}"
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun enablePayButton(selected: Int, total: Int) {
        gv_pay_button.isEnabled = true
        updatePayButtonTitle(selected, total)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun disablePayButton(selected: Int, total: Int) {
        gv_pay_button.isEnabled = false
        updatePayButtonTitle(selected, total)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun showSelectedLineItemsSum(integralPart: String, fractionalPart: String) {
        (gv_line_items.adapter as LineItemsAdapter?)?.totalGrossPriceIntegralAndFractionalParts =
                Pair(integralPart, fractionalPart)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun showReturnReasonDialog(reasons: List<String>,
                                        resultCallback: ReturnReasonDialogResultCallback) {
        fragmentManager?.let { fm ->
            ReturnReasonDialog.createInstance(reasons).run {
                callback = resultCallback
                show(fm, TAG_RETURN_REASON_DIALOG)
            }
        }
    }

    private fun updatePayButtonTitle(selected: Int, total: Int) {
        @SuppressLint("SetTextI18n")
        gv_pay_button.text = resources.getString(R.string.gv_digital_invoice_pay, selected, total)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun setPresenter(presenter: DigitalInvoiceScreenContract.Presenter) {
        this.presenter = presenter
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onStart() {
        super.onStart()
        presenter?.start()
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onStop() {
        super.onStop()
        presenter?.stop()
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onLineItemClicked(lineItem: SelectableLineItem) {
        presenter?.editLineItem(lineItem)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onLineItemSelected(lineItem: SelectableLineItem) {
        presenter?.selectLineItem(lineItem)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onLineItemDeselected(lineItem: SelectableLineItem) {
        presenter?.deselectLineItem(lineItem)
    }

    /**
     * Internal use only.
     *
     * @suppress
     */
    override fun onWhatIsThisButtonClicked() {
        fragmentManager?.let { fm ->
            WhatIsThisDialog.createInstance().run {
                callback = { isHelpful ->
                    if (isHelpful != null) {
                        presenter?.userFeedbackReceived(isHelpful)
                    }
                }
                show(fm, TAG_WHAT_IS_THIS_DIALOG)
            }
        }
    }

    override fun updateLineItem(selectableLineItem: SelectableLineItem) {
        presenter?.updateLineItem(selectableLineItem)
    }

}
