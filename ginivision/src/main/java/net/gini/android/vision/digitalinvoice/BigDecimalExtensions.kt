package net.gini.android.vision.digitalinvoice

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
internal fun BigDecimal.integralPartWithCurrency(currency: Currency, decimalFormat: DecimalFormat): String {
    val sign = if (this.compareTo(BigDecimal.ZERO) == -1) {
        "-"
    } else {
        ""
    }
    return "$sign${currency.symbol}${this.abs().integralPart(decimalFormat)}"
}

@JvmSynthetic
internal fun BigDecimal.integralPart(decimalFormat: DecimalFormat): String = decimalFormat.format(this.toBigInteger())

@JvmSynthetic
internal fun BigDecimal.fractionalPart(decimalFormat: DecimalFormat): String = decimalFormat.format(this.remainder(BigDecimal.ONE).abs())

@JvmSynthetic
internal val PRICE_STRING_FORMAT = DecimalFormat("0.00",
        DecimalFormatSymbols.getInstance(Locale.ENGLISH)).apply { isParseBigDecimal = true }

@JvmSynthetic
internal fun BigDecimal.toPriceString(currency: String) = "${PRICE_STRING_FORMAT.format(this)}:$currency"

@JvmSynthetic
internal val PRICE_STRING_REGEX = "^-?[0-9]+([.,])[0-9]+\$".toRegex()

@JvmSynthetic
internal fun parsePriceString(priceString: String): Triple<BigDecimal, String, Currency?> =
        priceString.split(":").let { substrings ->
            if (substrings.size != 2) {
                throw java.lang.NumberFormatException(
                        "Invalid price format. Expected <Price>:<Currency Code>, but got: $priceString")
            }
            val price = parsePrice(substrings[0])
            val rawCurrency = substrings[1]
            val currency = Currency.getInstance(substrings[1])
            return Triple(price, rawCurrency, currency)
        }

private fun parsePrice(price: String): BigDecimal =
        if (price matches PRICE_STRING_REGEX) {
            when {
                price.contains(".") -> {
                    parsePriceWithLocale(price, Locale.ENGLISH)
                }
                price.contains(",") -> {
                    parsePriceWithLocale(price, Locale.GERMAN)
                }
                else -> {
                    throw NumberFormatException("Unknown number format locale")
                }
            }
        } else {
            throw NumberFormatException("Invalid number format")
        }

private fun parsePriceWithLocale(price: String, locale: Locale) = DecimalFormat("0.00",
        DecimalFormatSymbols.getInstance(locale))
        .apply { isParseBigDecimal = true }
        .run {
            try {
                parse(price) as BigDecimal
            } catch (e: ParseException) {
                throw NumberFormatException(e.message)
            }
        }