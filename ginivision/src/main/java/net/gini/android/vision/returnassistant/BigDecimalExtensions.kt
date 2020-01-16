package net.gini.android.vision.returnassistant

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Alpar Szotyori on 11.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */
fun BigDecimal.integralPartWithCurrency(currency: Currency, decimalFormat: DecimalFormat): String =
        "${currency.symbol}${this.integralPart(decimalFormat)}"

fun BigDecimal.integralPart(decimalFormat: DecimalFormat): String = decimalFormat.format(this.toBigInteger())

fun BigDecimal.fractionPart(decimalFormat: DecimalFormat): String = decimalFormat.format(this.remainder(BigDecimal.ONE).abs())