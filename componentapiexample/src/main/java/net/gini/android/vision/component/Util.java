package net.gini.android.vision.component;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Set;

final class Util {

    static boolean isIntentActionViewOrSend(@NonNull final Intent intent) {
        String action = intent.getAction();
        return Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEND.equals(action);
    }

    static boolean hasNoPay5Extractions(final Set<String> extractionNames) {
        for (String extractionName : extractionNames) {
            if (isPay5Extraction(extractionName)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPay5Extraction(String extractionName) {
        return extractionName.equals("amountToPay") ||
                extractionName.equals("bic") ||
                extractionName.equals("iban") ||
                extractionName.equals("paymentReference") ||
                extractionName.equals("paymentRecipient");
    }
}
