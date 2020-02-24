package net.gini.android.vision.digitalinvoice

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.util.*

/**
 * Created by Alpar Szotyori on 11.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

val RAW_GROSS_PRICE_FORMAT = DecimalFormat("0.00",
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)).apply { isParseBigDecimal = true }

val GROSS_PRICE_STRING_REGEX = "^[0-9]+([.,])[0-9]+\$".toRegex()

@Parcelize
class LineItem(
        val id: String,
        val description: String,
        val quantity: Int,
        val rawGrossPrice: String
) : Parcelable {

    @IgnoredOnParcel
    val grossPrice: BigDecimal
    @IgnoredOnParcel
    val totalGrossPrice: BigDecimal
    @IgnoredOnParcel
    val currency: Currency?
    @IgnoredOnParcel
    val rawCurrency: String

    companion object {
        fun createRawGrossPrice(grossPrice: BigDecimal, currency: String) = "${RAW_GROSS_PRICE_FORMAT.format(grossPrice)}:$currency"
    }

    init {
        rawGrossPrice.split(":").let {
            check(it.size == 2) {
                "Invalid gross price format. Expected <Gross Price>:<Currency Code>, but got: $rawGrossPrice"
            }
            grossPrice = try {
                parseGrossPriceString(it[0])
            } catch (_: NumberFormatException) {
                BigDecimal.ZERO
            }
            totalGrossPrice = grossPrice.times(BigDecimal(quantity))
            rawCurrency = it[1]
            currency = try {
                Currency.getInstance(it[1])
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun parseGrossPriceString(grossPrice: String): BigDecimal =
            if (grossPrice matches GROSS_PRICE_STRING_REGEX) {
                when {
                    grossPrice.contains(".") -> {
                        parseGrossPriceWithLocale(grossPrice, Locale.ENGLISH)
                    }
                    grossPrice.contains(",") -> {
                        parseGrossPriceWithLocale(grossPrice, Locale.GERMAN)
                    }
                    else -> {
                        throw NumberFormatException()
                    }
                }
            } else {
                throw NumberFormatException()
            }

    private fun parseGrossPriceWithLocale(grossPrice: String, locale: Locale) = DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(locale))
            .apply { isParseBigDecimal = true }
            .run {
                try {
                    parse(grossPrice) as BigDecimal
                } catch (_: ParseException) {
                    throw NumberFormatException()
                }
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