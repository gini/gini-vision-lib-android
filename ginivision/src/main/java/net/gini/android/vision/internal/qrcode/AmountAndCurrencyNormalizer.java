package net.gini.android.vision.internal.qrcode;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Alpar Szotyori on 12.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

/**
 * Normalizes amount and currency strings to conform to the Gini API amount format: 25.79:EUR
 */
final class AmountAndCurrencyNormalizer {

    /**
     * Creates an amount string in the Gini API format: 25.79:EUR
     *
     * @param amount an amount string
     * @param currency a currency code
     * @return normalized amount string in the Gini API format or an empty string
     */
    @NonNull
    static String normalizeAmount(@Nullable final String amount, @NonNull final String currency) {
        if (!TextUtils.isEmpty(amount)) {
            try {
                BigDecimal amountBigDecimal = new BigDecimal(amount.replace(",", "."));
                if (amountBigDecimal.scale() < 2) {
                    amountBigDecimal = amountBigDecimal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
                }
                return amountBigDecimal.toPlainString() + ":" + currency;
            } catch (final NumberFormatException ignored) {
            }
        }
        return "";
    }

    /**
     * Normalizes currency codes.
     *
     * @param currency a currency code
     * @return normalized currency code or an empty string
     */
    @NonNull
    static String normalizeCurrency(@Nullable final String currency) {
        return TextUtils.isEmpty(currency) ? "" : currency.toUpperCase(Locale.ENGLISH);
    }

    private AmountAndCurrencyNormalizer() {
    }
}
