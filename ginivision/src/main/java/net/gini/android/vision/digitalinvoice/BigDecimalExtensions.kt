package net.gini.android.vision.digitalinvoice

import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*

/**
 * Created by Alpar Szotyori on 11.12.2019.
 *
 * Copyright (c) 2019 Gini GmbH.
 */

@JvmSynthetic
internal fun BigDecimal.integralPartWithCurrency(currency: Currency, decimalFormat: DecimalFormat): String =
        "${currency.symbol}${this.integralPart(decimalFormat)}"

@JvmSynthetic
internal fun BigDecimal.integralPart(decimalFormat: DecimalFormat): String = decimalFormat.format(this.toBigInteger())

@JvmSynthetic
internal fun BigDecimal.fractionalPart(decimalFormat: DecimalFormat): String = decimalFormat.format(this.remainder(BigDecimal.ONE).abs())