package net.gini.android.vision.returnassistant

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.gv_item_return_assistant_footer.view.*
import kotlinx.android.synthetic.main.gv_item_return_assistant_header.view.*
import kotlinx.android.synthetic.main.gv_item_return_assistant_line_item.view.*
import net.gini.android.vision.R
import net.gini.android.vision.returnassistant.ViewType.*
import net.gini.android.vision.returnassistant.ViewType.LineItem

/**
 * Created by Alpar Szotyori on 11.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

interface LineItemsAdapterListener {
    fun onLineItemClicked(lineItem: SelectableLineItem)
    fun onLineItemSelected(lineItem: SelectableLineItem)
    fun onLineItemDeselected(lineItem: SelectableLineItem)
}

class LineItemsAdapter(context: Context, val listener: LineItemsAdapterListener) :
        RecyclerView.Adapter<ViewHolder<*>>() {

    var lineItems: List<SelectableLineItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var selectedAndTotalItems: String = ""
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var totalAmountIntegralAndFractionParts: Pair<String, String> = Pair("", "")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewTypeId: Int) =
            ViewHolder.forViewTypeId(viewTypeId, layoutInflater, parent)

    override fun getItemCount(): Int = lineItems.size + 2

    private fun footerPosition() = lineItems.size + 1

    override fun getItemViewType(position: Int): Int {
        println("position: $position")
        val id = when (position) {
            0 -> Header.id
            footerPosition() -> Footer.id
            else -> LineItem.id
        }
        println("viewType: ${ViewType.from(id)}")
        return id
    }

    override fun onBindViewHolder(viewHolder: ViewHolder<*>, position: Int) {
        when (viewHolder) {
            is ViewHolder.HeaderViewHolder -> viewHolder.bind(selectedAndTotalItems)
            is ViewHolder.LineItemViewHolder -> {
                lineItemForPosition(position, lineItems)?.let {
                    viewHolder.listener = listener
                    viewHolder.bind(it, lineItems)
                }
            }
            is ViewHolder.FooterViewHolder -> viewHolder.bind(totalAmountIntegralAndFractionParts)
        }
    }

    override fun onViewRecycled(viewHolder: ViewHolder<*>) {
        viewHolder.unbind()
    }
}

fun lineItemForPosition(position: Int,
                        lineItems: List<SelectableLineItem>): SelectableLineItem? =
        lineItems.getOrElse(position - 1) { null }


sealed class ViewType {
    abstract val id: Int

    object Header : ViewType() {
        override val id: Int = 1
    }

    object LineItem : ViewType() {
        override val id: Int = 2
    }

    object Footer : ViewType() {
        override val id: Int = 3
    }

    companion object {
        fun from(viewTypeId: Int): ViewType = when (viewTypeId) {
            1 -> Header
            2 -> LineItem
            3 -> Footer
            else -> throw IllegalStateException("Unknow adapter view type id: $viewTypeId")
        }
    }
}

sealed class ViewHolder<in T>(itemView: View, val viewType: ViewType) :
        RecyclerView.ViewHolder(itemView) {

    abstract fun bind(data: T, allData: List<T>? = null)

    abstract fun unbind()

    class HeaderViewHolder(itemView: View) : ViewHolder<String>(itemView, Header) {
        private val selectedAndTotalItems = itemView.gv_selected_and_total_items

        override fun bind(data: String, allData: List<String>?) {
            selectedAndTotalItems.text = data
        }

        override fun unbind() {
        }
    }

    class LineItemViewHolder(itemView: View) : ViewHolder<SelectableLineItem>(itemView, LineItem) {
        private val checkbox: CheckBox = itemView.gv_checkbox
        private val description: TextView = itemView.gv_description
        private val quantity: TextView = itemView.gv_quantity
        private val priceIntegralPart: TextView = itemView.gv_amount_integral_part
        private val priceFractionPart: TextView = itemView.gv_amount_fraction_part
        var listener: LineItemsAdapterListener? = null

        override fun bind(data: SelectableLineItem, allData: List<SelectableLineItem>?) {
            checkbox.isChecked = data.selected
            data.lineItem.let { li ->
                description.text = li.description
                quantity.text = li.quantity.toString()
                lineItemTotalAmountIntegralAndFractionParts(li).let { (integral, fraction) ->
                    priceIntegralPart.text = integral
                    @SuppressLint("SetTextI18n")
                    priceFractionPart.text = fraction
                }
            }
            itemView.setOnClickListener {
                allData?.let {
                    lineItemForPosition(adapterPosition, allData)?.let {
                        listener?.onLineItemClicked(it)
                    }
                }
            }
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                allData?.let {
                    lineItemForPosition(adapterPosition, allData)?.let {
                        if (it.selected != isChecked) {
                            listener?.apply {
                                if (isChecked) {
                                    onLineItemSelected(it)
                                } else {
                                    onLineItemDeselected(it)
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun unbind() {
            listener = null
            itemView.setOnClickListener(null)
            checkbox.setOnCheckedChangeListener(null)
        }
    }

    class FooterViewHolder(itemView: View) : ViewHolder<Pair<String, String>>(itemView, Footer) {
        private val integralPart = itemView.gv_amount_total_integral_part
        private val fractionPart = itemView.gv_amount_total_fraction_part

        override fun bind(data: Pair<String, String>, allData: List<Pair<String, String>>?) {
            val (integral, fraction) = data
            integralPart.text = integral
            fractionPart.text = fraction
        }

        override fun unbind() {
        }
    }

    companion object {
        fun forViewTypeId(viewTypeId: Int, layoutInflater: LayoutInflater,
                          parent: ViewGroup) = when (ViewType.from(viewTypeId)) {
            Header -> HeaderViewHolder(
                    layoutInflater.inflate(R.layout.gv_item_return_assistant_header, parent, false))
            LineItem -> LineItemViewHolder(
                    layoutInflater.inflate(R.layout.gv_item_return_assistant_line_item, parent,
                            false))
            Footer -> FooterViewHolder(
                    layoutInflater.inflate(R.layout.gv_item_return_assistant_footer, parent, false))
        }
    }
}
