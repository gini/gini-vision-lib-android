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

val RAW_AMOUNT_FORMAT = DecimalFormat("0.00",
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)).apply { isParseBigDecimal = true }

val AMOUNT_STRING_REGEX = "^[0-9]+([.,])[0-9]+\$".toRegex()

@Parcelize
class LineItem(
        val id: String,
        val description: String,
        val quantity: Int,
        val rawAmount: String
) : Parcelable {

    @IgnoredOnParcel
    val amount: BigDecimal
    @IgnoredOnParcel
    val totalAmount: BigDecimal
    @IgnoredOnParcel
    val currency: Currency?
    @IgnoredOnParcel
    val rawCurrency: String

    companion object {
        fun createRawAmount(amount: BigDecimal, currency: String) = "${RAW_AMOUNT_FORMAT.format(
                amount)}:$currency"
    }

    init {
        rawAmount.split(":").let {
            check(it.size == 2) {
                "Invalid amount format. Expected <Amount>:<Currency Code>, but got: $rawAmount"
            }
            amount = try {
                parseAmountString(it[0])
            } catch (_: NumberFormatException) {
                BigDecimal.ZERO
            }
            totalAmount = amount.times(BigDecimal(quantity))
            rawCurrency = it[1]
            currency = try {
                Currency.getInstance(it[1])
            } catch (e: Exception) {
                null
            }
        }
    }

    private fun parseAmountString(amount: String): BigDecimal =
            if (amount matches AMOUNT_STRING_REGEX) {
                when {
                    amount.contains(".") -> {
                        parseAmountWithLocale(amount, Locale.ENGLISH)
                    }
                    amount.contains(",") -> {
                        parseAmountWithLocale(amount, Locale.GERMAN)
                    }
                    else -> {
                        throw NumberFormatException()
                    }
                }
            } else {
                throw NumberFormatException()
            }

    private fun parseAmountWithLocale(amount: String, locale: Locale) = DecimalFormat("0.00",
            DecimalFormatSymbols.getInstance(locale))
            .apply { isParseBigDecimal = true }
            .run {
                try {
                    parse(amount) as BigDecimal
                } catch (_: ParseException) {
                    throw NumberFormatException()
                }
            }

    override fun toString() = "LineItem(id=$id, description=$description, quantity=$quantity, rawAmount=$rawAmount, amount=$amount, totalAmount=$totalAmount, currency=$currency)"

    override fun equals(other: Any?) = other is LineItem
            && id == other.id
            && description == other.description
            && quantity == other.quantity
            && amount == other.amount
            && totalAmount == other.totalAmount
            && rawAmount == other.rawAmount
            && currency == other.currency

    override fun hashCode() = Objects.hash(id, description, quantity, rawAmount, amount,
            totalAmount, currency)

    @JvmSynthetic
    fun copy(id: String = this.id, description: String = this.description,
             quantity: Int = this.quantity, rawAmount: String = this.rawAmount) =
            LineItem(id, description, quantity, rawAmount)

}