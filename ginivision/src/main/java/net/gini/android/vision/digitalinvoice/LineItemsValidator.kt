package net.gini.android.vision.digitalinvoice

import net.gini.android.vision.digitalinvoice.DigitalInvoiceException.*
import net.gini.android.vision.network.model.GiniVisionCompoundExtraction

/**
 * Created by Alpar Szotyori on 10.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

internal typealias Validate = (compoundExtractions: Map<String, GiniVisionCompoundExtraction>) -> Unit

/**
 * Internal use only.
 *
 * @suppress
 */
internal class LineItemsValidator {

    companion object {

        /**
         * Internal use only.
         *
         * @suppress
         */
        @JvmStatic
        @Throws(LineItemsMissingException::class, DescriptionMissingException::class, QuantityMissingException::class,
                GrossPriceMissingException::class, ArticleNumberMissingException::class, MixedCurrenciesException::class,
                QuantityParsingException::class, GrossPriceParsingException::class)
        fun validate(compoundExtractions: Map<String, GiniVisionCompoundExtraction>) = listOf(
                lineItemsAvailable,
                descriptionAvailable,
                quantityAvailable,
                grossPriceAvailable,
                articleNumberAvailable,
                quantityParcelable,
                grossPriceParcelable,
                singleCurrency
        ).forEach { it(compoundExtractions) }
    }
}

internal val lineItemsAvailable: Validate = { compoundExtractions ->
    if (!compoundExtractions.containsKey("lineItems")) {
        throw LineItemsMissingException()
    }
}

internal val descriptionAvailable: Validate = { compoundExtractions ->
    if ((compoundExtractions["lineItems"]?.specificExtractionMaps?.all { it.containsKey("description") }) != true) {
        throw DescriptionMissingException()
    }
}

internal val quantityAvailable: Validate = { compoundExtractions ->
    if ((compoundExtractions["lineItems"]?.specificExtractionMaps?.all { it.containsKey("quantity") }) != true) {
        throw QuantityMissingException()
    }
}

internal val grossPriceAvailable: Validate = { compoundExtractions ->
    if ((compoundExtractions["lineItems"]?.specificExtractionMaps?.all { it.containsKey("baseGross") }) != true) {
        throw GrossPriceMissingException()
    }
}

internal val articleNumberAvailable: Validate = { compoundExtractions ->
    if ((compoundExtractions["lineItems"]?.specificExtractionMaps?.all { it.containsKey("artNumber") }) != true) {
        throw ArticleNumberMissingException()
    }
}

internal val quantityParcelable: Validate = { compoundExtractions ->
    if ((compoundExtractions["lineItems"]?.specificExtractionMaps?.all { it["quantity"]?.value?.toIntOrNull() != null }) != true) {
        throw QuantityParsingException()
    }
}

internal val grossPriceParcelable: Validate = { compoundExtractions ->
    compoundExtractions["lineItems"]?.specificExtractionMaps?.forEach { map ->
        map["baseGross"]?.value?.let { grossPriceString ->
            try {
                parsePriceString(grossPriceString)
            } catch (e: Exception) {
                throw GrossPriceParsingException(cause = e)
            }
        } ?: throw GrossPriceParsingException(cause = GrossPriceMissingException())
    } ?: throw GrossPriceParsingException(cause = LineItemsMissingException())
}

internal val singleCurrency: Validate = { compoundExtractions ->
    compoundExtractions["lineItems"]?.specificExtractionMaps?.let { lineItemRows ->
        if (lineItemRows.isEmpty()) {
            throw MixedCurrenciesException(cause = LineItemsMissingException())
        } else {
            val firstCurrency = try {
                lineItemRows[0]["baseGross"]?.value?.let { grossPriceString ->
                    val (_, _, currency) = parsePriceString(grossPriceString)
                    currency
                }
            } catch (e: Exception) {
                throw MixedCurrenciesException(cause = GrossPriceParsingException(cause = e))
            }
            if (firstCurrency == null) {
                throw MixedCurrenciesException(cause = GrossPriceMissingException())
            } else {
                val sameCurrency = lineItemRows.subList(1, lineItemRows.size).fold(true, { sameCurrency, row ->
                    val currency = try {
                        row["baseGross"]?.value?.let { grossPriceString ->
                            val (_, _, currency) = parsePriceString(grossPriceString)
                            currency
                        }
                    } catch (e: Exception) {
                        throw MixedCurrenciesException(cause = GrossPriceParsingException(cause = e))
                    }
                    sameCurrency && (firstCurrency == currency)
                })
                if (!sameCurrency) {
                    throw MixedCurrenciesException()
                }
            }
        }
    } ?: throw MixedCurrenciesException(cause = LineItemsMissingException())
}