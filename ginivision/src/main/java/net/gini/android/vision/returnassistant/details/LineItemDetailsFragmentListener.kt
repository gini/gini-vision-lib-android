package net.gini.android.vision.returnassistant.details

import net.gini.android.vision.returnassistant.SelectableLineItem

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
interface LineItemDetailsFragmentListener {

    fun save(selectableLineItem: SelectableLineItem)
}