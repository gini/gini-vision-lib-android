package net.gini.android.vision.digitalinvoice.details

import net.gini.android.vision.digitalinvoice.SelectableLineItem

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface LineItemDetailsFragmentListener {

    fun onSave(selectableLineItem: SelectableLineItem)
}