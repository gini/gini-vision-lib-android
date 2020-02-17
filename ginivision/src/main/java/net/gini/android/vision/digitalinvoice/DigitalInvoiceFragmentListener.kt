package net.gini.android.vision.digitalinvoice

import net.gini.android.vision.network.model.GiniVisionCompoundExtraction
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction

/**
 * Created by Alpar Szotyori on 05.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface DigitalInvoiceFragmentListener {

    fun onEditLineItem(selectableLineItem: SelectableLineItem)

    fun onPayInvoice(selectedLineItems: List<LineItem>,
                     selectedLineItemsTotalPrice: String,
                     deselectedLineItems: List<LineItem>,
                     reviewedCompoundExtractions: Map<String, GiniVisionCompoundExtraction>,
                     reviewedExtractions: Map<String, GiniVisionSpecificExtraction>)
}