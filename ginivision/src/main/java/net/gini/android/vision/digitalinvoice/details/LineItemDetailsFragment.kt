package net.gini.android.vision.digitalinvoice.details


import android.app.Activity
import android.os.Bundle
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.gv_fragment_line_item_details.*
import net.gini.android.vision.R
import net.gini.android.vision.digitalinvoice.LineItem
import net.gini.android.vision.digitalinvoice.ReturnReasonDialog
import net.gini.android.vision.digitalinvoice.ReturnReasonDialogResultCallback
import net.gini.android.vision.digitalinvoice.SelectableLineItem

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

private const val ARG_SELECTABLE_LINE_ITEM = "GV_ARG_SELECTABLE_LINE_ITEM"
private const val TAG_RETURN_REASON_DIALOG = "TAG_RETURN_REASON_DIALOG"

class LineItemDetailsFragment : Fragment(), LineItemDetailsScreenContract.View,
        LineItemDetailsFragmentInterface {

    override var listener: LineItemDetailsFragmentListener?
        get() = this.presenter?.listener
        set(value) {
            this.presenter?.listener = value
        }

    private var presenter: LineItemDetailsScreenContract.Presenter? = null

    private lateinit var lineItem: SelectableLineItem

    private var descriptionTextWatcher: TextWatcher? = null
    private var quantityTextWatcher: TextWatcher? = null
    private var grossPriceTextWatcher: TextWatcher? = null

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
            lineItem = it.getParcelable(ARG_SELECTABLE_LINE_ITEM) ?: SelectableLineItem(
                    selected = false,
                    lineItem = LineItem("", "", 0, ""))
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setInputHandlers()
    }

    private fun setInputHandlers() {
        gv_checkbox.setOnCheckedChangeListener { _, isChecked ->
            presenter?.let {
                if (isChecked) {
                    it.selectLineItem()
                } else {
                    it.deselectLineItem()
                }
            }
        }
        descriptionTextWatcher = gv_description.doAfterTextChanged {
            presenter?.setDescription(it)
        }
        quantityTextWatcher = gv_quantity.doAfterTextChanged {
            presenter?.setQuantity(try {
                it.toInt()
            } catch (_: NumberFormatException) {
                0
            })
        }
        grossPriceTextWatcher = gv_gross_price.doAfterTextChanged {
            presenter?.setGrossPrice(it)
        }
        gv_save_button.setOnClickListener {
            presenter?.save()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        gv_description.removeTextChangedListener(descriptionTextWatcher)
        gv_quantity.removeTextChangedListener(quantityTextWatcher)
        gv_gross_price.removeTextChangedListener(grossPriceTextWatcher)
    }

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

    override fun showDescription(description: String) {
        gv_description.setText(description)
    }

    override fun showQuantity(quantity: Int) {
        gv_quantity.setText(quantity.toString())
    }

    override fun showGrossPrice(displayedGrossPrice: String, currency: String) {
        gv_gross_price.setText(displayedGrossPrice)
        gv_currency.text = currency
    }

    override fun showTotalGrossPrice(integralPart: String, fractionalPart: String) {
        gv_gross_price_total_integral_part.text = integralPart
        gv_gross_price_total_fractional_part.text = fractionalPart
    }

    override fun enableSaveButton() {
        gv_save_button.isEnabled = true
    }

    override fun disableSaveButton() {
        gv_save_button.isEnabled = false
    }

    override fun enableInput() {
        gv_description.isEnabled = true
        gv_quantity.isEnabled = true
        gv_gross_price.isEnabled = true
        gv_currency.isEnabled = true
    }

    override fun disableInput() {
        gv_description.isEnabled = false
        gv_quantity.isEnabled = false
        gv_gross_price.isEnabled = false
        gv_currency.isEnabled = false
    }

    override fun showReturnReasonDialog(reasons: List<String>,
                                        resultCallback: ReturnReasonDialogResultCallback) {
        fragmentManager?.let { fm ->
            ReturnReasonDialog.createInstance(reasons).run {
                callback = resultCallback
                show(fm, TAG_RETURN_REASON_DIALOG)
            }
        }
    }

    override fun showCheckbox(selected: Boolean, quantity: Int) {
        gv_checkbox.isChecked = selected
        gv_checkbox.text =
                resources.getQuantityString(
                        R.plurals.gv_digital_invoice_line_item_details_selected_line_items,
                        quantity, quantity, if (selected) resources.getString(
                        R.string.gv_digital_invoice_line_item_details_selected) else "")
    }

}
