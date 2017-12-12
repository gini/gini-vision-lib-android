package net.gini.android.vision.internal.qrcode;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by Alpar Szotyori on 12.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

final class AmountAndCurrencyNormalizer {

    @NonNull
    static String normalizeAmount(@Nullable final String amount, @NonNull final String currency) {
        if (TextUtils.isEmpty(amount)) {
            return "";
        }
        try {
            BigDecimal amountBigDecimal = new BigDecimal(amount.replace(",", "."));
            if (amountBigDecimal.scale() < 2) {
                amountBigDecimal = amountBigDecimal.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            }
            return amountBigDecimal.toPlainString() + ":" + currency;
        } catch (final NumberFormatException ignored) {
        }
        return "";
    }

    @NonNull
    static String normalizeCurrency(@Nullable final String currency) {
        return TextUtils.isEmpty(currency) ? "" : currency.toUpperCase(Locale.ENGLISH);
    }

    private AmountAndCurrencyNormalizer() {
    }
}
