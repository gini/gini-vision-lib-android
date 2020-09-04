package net.gini.android.vision.digitalinvoice

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.util.*

/**
 * Created by Alpar Szotyori on 11.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

/**
 * The `LineItem` class contains information from a line item extraction.
 */
@Parcelize
class LineItem(
        val id: String,
        val description: String,
        val quantity: Int,
        val rawGrossPrice: String
) : Parcelable {

    /**
     * The unit price.
     */
    @IgnoredOnParcel
    val grossPrice: BigDecimal

    /**
     * The total unit price. Total unit price = unit price x quantity.
     */
    @IgnoredOnParcel
    val totalGrossPrice: BigDecimal

    /**
     * The parsed currency.
     */
    @IgnoredOnParcel
    val currency: Currency?

    /**
     * The currency as a string in ISO 4217 format.
     */
    @IgnoredOnParcel
    val rawCurrency: String

    init {
        val (grossPrice, rawCurrency, currency) = try {
            parsePriceString(rawGrossPrice)
        } catch (e: Exception) {
            Triple(BigDecimal.ZERO, "", null)
        }
        this.grossPrice = grossPrice
        this.totalGrossPrice = grossPrice.times(BigDecimal(quantity))
        this.rawCurrency = rawCurrency
        this.currency = currency
    }

    override fun toString() = "LineItem(id=$id, description=$description, quantity=$quantity, rawGrossPrice=$rawGrossPrice, grossPrice=$grossPrice, totalGrossPrice=$totalGrossPrice, currency=$currency)"

    override fun equals(other: Any?) = other is LineItem
            && id == other.id
            && description == other.description
            && quantity == other.quantity
            && grossPrice == other.grossPrice
            && totalGrossPrice == other.totalGrossPrice
            && rawGrossPrice == other.rawGrossPrice
            && currency == other.currency

    override fun hashCode() = Objects.hash(id, description, quantity, rawGrossPrice, grossPrice,
            totalGrossPrice, currency)

    @JvmSynthetic
    fun copy(id: String = this.id, description: String = this.description,
             quantity: Int = this.quantity, rawGrossPrice: String = this.rawGrossPrice) =
            LineItem(id, description, quantity, rawGrossPrice)

}