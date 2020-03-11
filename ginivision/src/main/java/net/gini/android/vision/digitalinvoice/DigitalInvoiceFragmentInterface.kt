package net.gini.android.vision.digitalinvoice

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface DigitalInvoiceFragmentInterface {

    var listener: DigitalInvoiceFragmentListener?

    fun updateLineItem(selectableLineItem: SelectableLineItem)
}