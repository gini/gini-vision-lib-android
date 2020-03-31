package net.gini.android.vision.digitalinvoice

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * The `SelectableLineItem` wrapps a [LineItem] and adds the possibility to select/deselect it and also add a reason why it's deselected.
 *
 * @property reason Reason for deselection.
 */
@Parcelize
class SelectableLineItem(
        var selected: Boolean = true,
        var reason: String? = null,
        val lineItem: LineItem
) : Parcelable {

    override fun toString() = "LineItem(selected=$selected, reason=$reason, lineItem=$lineItem)"

    override fun equals(other: Any?) = other is SelectableLineItem
            && selected == other.selected
            && reason == other.reason
            && lineItem == other.lineItem

    override fun hashCode() = Objects.hash(selected, lineItem)

    @JvmSynthetic
    fun copy(selected: Boolean = this.selected,
             reason: String? = this.reason,
             lineItem: LineItem = this.lineItem) = SelectableLineItem(selected, reason,
            lineItem.copy())
}