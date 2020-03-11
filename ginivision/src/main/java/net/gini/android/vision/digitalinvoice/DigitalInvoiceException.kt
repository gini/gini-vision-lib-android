package net.gini.android.vision.digitalinvoice

/**
 * Created by Alpar Szotyori on 10.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

sealed class DigitalInvoiceException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {
    class LineItemsMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
    class DescriptionMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
    class QuantityMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
    class GrossPriceMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
    class ArticleNumberMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
    class MixedCurrenciesException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
    class QuantityParsingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
    class GrossPriceParsingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
}