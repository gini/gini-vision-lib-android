package net.gini.android.vision.example;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.Set;

public final class ExampleUtil {

    public static boolean isIntentActionViewOrSend(@NonNull final Intent intent) {
        String action = intent.getAction();
        return Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEND.equals(action);
    }

    public static boolean hasNoPay5Extractions(final Set<String> extractionNames) {
        for (String extractionName : extractionNames) {
            if (isPay5Extraction(extractionName)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPay5Extraction(String extractionName) {
        return extractionName.equals("amountToPay") ||
                extractionName.equals("bic") ||
                extractionName.equals("iban") ||
                extractionName.equals("paymentReference") ||
                extractionName.equals("paymentRecipient");
    }

    private ExampleUtil() {
    }
}
