package net.gini.android.vision.returnassistant

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.gv_item_return_assistant_line_item.view.*
import net.gini.android.vision.R

/**
 * Created by Alpar Szotyori on 11.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
class LineItemsAdapter(context: Context, val listener: LineItemsAdapterListener) :
        RecyclerView.Adapter<LineItemViewHolder>() {

    var lineItems: List<SelectableLineItem> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val layoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineItemViewHolder {
        return LineItemViewHolder(
                layoutInflater.inflate(R.layout.gv_item_return_assistant_line_item, parent,
                        false))
    }

    override fun getItemCount(): Int = lineItems.size

    override fun onBindViewHolder(viewHolder: LineItemViewHolder, position: Int) {
        lineItems[position].let {
            viewHolder.apply {
                checkbox.isChecked = it.selected
                it.lineItem.let { li ->
                    description.text = li.description
                    quantity.text = li.quantity.toString()
                    lineItemAmountIntegralAndFractionParts(li).let { (integral, fraction) ->
                        priceIntegralPart.text = integral
                        @SuppressLint("SetTextI18n")
                        priceFractionPart.text = fraction
                    }
                }
                itemView.setOnClickListener {
                    listener.onLineItemClicked(lineItems[adapterPosition])
                }
                checkbox.setOnCheckedChangeListener { _, isChecked ->
                    lineItems[adapterPosition].let {
                        if (it.selected != isChecked) {
                            listener.apply {
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
    }

}

class LineItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val checkbox: CheckBox = view.gv_checkbox
    val description: TextView = view.gv_description
    val quantity: TextView = view.gv_quantity
    val priceIntegralPart: TextView = view.gv_amount_integral_part
    val priceFractionPart: TextView = view.gv_amount_fraction_part
}

interface LineItemsAdapterListener {
    fun onLineItemClicked(lineItem: SelectableLineItem)
    fun onLineItemSelected(lineItem: SelectableLineItem)
    fun onLineItemDeselected(lineItem: SelectableLineItem)
}