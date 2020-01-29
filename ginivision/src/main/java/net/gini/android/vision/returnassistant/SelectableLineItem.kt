package net.gini.android.vision.returnassistant

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

/**
 * Created by Alpar Szotyori on 17.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
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