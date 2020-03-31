package net.gini.android.vision.digitalinvoice

import android.app.Activity
import androidx.annotation.VisibleForTesting
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

internal open class DigitalInvoiceScreenPresenter(activity: Activity,
                                                  view: DigitalInvoiceScreenContract.View,
                                                  val extractions: Map<String, GiniVisionSpecificExtraction> = emptyMap(),
                                                  val compoundExtractions: Map<String, GiniVisionCompoundExtraction> = emptyMap()) :
        DigitalInvoiceScreenContract.Presenter(activity, view) {

    override var listener: DigitalInvoiceFragmentListener? = null

    @VisibleForTesting
    val digitalInvoice: DigitalInvoice
    var returnReasons: List<String> = emptyList()

    init {
        view.setPresenter(this)
        digitalInvoice = DigitalInvoice(extractions, compoundExtractions)
    }

    override fun selectLineItem(lineItem: SelectableLineItem) {
        digitalInvoice.selectLineItem(lineItem)
        updateView()
    }

    override fun deselectLineItem(lineItem: SelectableLineItem) {
        if (returnReasons.isEmpty()) {
            digitalInvoice.deselectLineItem(lineItem, null)
        } else {
            view.showReturnReasonDialog(returnReasons) { selectedReason ->
                if (selectedReason != null) {
                    digitalInvoice.deselectLineItem(lineItem, selectedReason)
                } else {
                    digitalInvoice.selectLineItem(lineItem)
                }
            }
        }
        updateView()
    }

    override fun editLineItem(lineItem: SelectableLineItem) {
        listener?.onEditLineItem(lineItem)
    }

    override fun userFeedbackReceived(helpful: Boolean) {
        // TODO
    }

    override fun pay() {
        digitalInvoice.updateLineItemExtractionsWithReviewedLineItems()
        digitalInvoice.updateAmountToPayExtractionWithTotalGrossPrice()
        listener?.onPayInvoice(digitalInvoice.extractions, digitalInvoice.compoundExtractions)
    }

    override fun updateLineItem(selectableLineItem: SelectableLineItem) {
        digitalInvoice.updateLineItem(selectableLineItem)
        updateView()
    }

    override fun start() {
        updateView()
    }

    override fun stop() {
    }

    @VisibleForTesting
    internal fun updateView() {
        view.apply {
            showLineItems(digitalInvoice.selectableLineItems)
            digitalInvoice.selectedAndTotalLineItemsCount().let { (selected, total) ->
                showSelectedAndTotalLineItems(selected, total)
                if (selected > 0) {
                    enablePayButton(selected, total)
                } else {
                    disablePayButton(0, total)
                }
            }
            digitalInvoice.lineItemsTotalGrossPriceSumIntegralAndFractionalParts().let { (integral, fractional) ->
                showSelectedLineItemsSum(integral, fractional)
            }
        }
    }
}