package net.gini.android.vision.digitalinvoice

import net.gini.android.vision.digitalinvoice.details.LineItemDetailsFragment
import net.gini.android.vision.digitalinvoice.details.LineItemDetailsFragmentListener

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * Public API of the [DigitalInvoiceFragment].
 *
 */
interface DigitalInvoiceFragmentInterface {

    /**
     * Set a listener for digital invoice events.
     */
    var listener: DigitalInvoiceFragmentListener?

    /**
     * Call this method when the modified selectable line item was returned by the
     * [LineItemDetailsFragmentListener.onSave] method.
     *
     * @param selectableLineItem the [SelectableLineItem] which was updated in the [LineItemDetailsFragment]
     */
    fun updateLineItem(selectableLineItem: SelectableLineItem)

}