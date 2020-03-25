package net.gini.android.vision.digitalinvoice

/**
 * Created by Alpar Szotyori on 10.03.2020.
 *
 * Copyright (c) 2020 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
internal sealed class DigitalInvoiceException(message: String? = null, cause: Throwable? = null) : Exception(message, cause) {

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class LineItemsMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class DescriptionMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class QuantityMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class GrossPriceMissingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class ArticleNumberMissingException(message: String? = null, cause: Throwable? = null) :
            DigitalInvoiceException(message, cause)

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class MixedCurrenciesException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class QuantityParsingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)

    /**
     * Internal use only.
     *
     * @suppress
     */
    internal class GrossPriceParsingException(message: String? = null, cause: Throwable? = null) : DigitalInvoiceException(message, cause)
}