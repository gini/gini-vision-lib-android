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

@JvmSynthetic
internal val RAW_GROSS_PRICE_FORMAT = DecimalFormat("0.00",
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)).apply { isParseBigDecimal = true }

@JvmSynthetic
internal val GROSS_PRICE_STRING_REGEX = "^[0-9]+([.,])[0-9]+\$".toRegex()

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

    companion object {

        @JvmSynthetic
        internal fun createRawGrossPrice(grossPrice: BigDecimal, currency: String) = "${RAW_GROSS_PRICE_FORMAT.format(
                grossPrice)}:$currency"


        @Throws(NumberFormatException::class, IllegalArgumentException::class)
        @JvmSynthetic
        internal fun parseGrossPriceExtraction(rawGrossPrice: String): Triple<BigDecimal, String, Currency?> {
            rawGrossPrice.split(":").let { substrings ->
                if (substrings.size != 2) {
                    throw java.lang.NumberFormatException(
                            "Invalid gross price format. Expected <Gross Price>:<Currency Code>, but got: $rawGrossPrice")
                }
                val grossPrice = parseGrossPrice(substrings[0])
                val rawCurrency = substrings[1]
                val currency = Currency.getInstance(substrings[1])
                return Triple(grossPrice, rawCurrency, currency)
            }
        }

        private fun parseGrossPrice(grossPrice: String): BigDecimal =
                if (grossPrice matches GROSS_PRICE_STRING_REGEX) {
                    when {
                        grossPrice.contains(".") -> {
                            parseGrossPriceWithLocale(grossPrice, Locale.ENGLISH)
                        }
                        grossPrice.contains(",") -> {
                            parseGrossPriceWithLocale(grossPrice, Locale.GERMAN)
                        }
                        else -> {
                            throw NumberFormatException("Unknown number format locale")
                        }
                    }
                } else {
                    throw NumberFormatException("Invalid number format")
                }

        private fun parseGrossPriceWithLocale(grossPrice: String, locale: Locale) = DecimalFormat("0.00",
                DecimalFormatSymbols.getInstance(locale))
                .apply { isParseBigDecimal = true }
                .run {
                    try {
                        parse(grossPrice) as BigDecimal
                    } catch (e: ParseException) {
                        throw NumberFormatException(e.message)
                    }
                }
    }

    init {
        val (grossPrice, rawCurrency, currency) = try {
            parseGrossPriceExtraction(rawGrossPrice)
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