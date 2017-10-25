package net.gini.android.vision.screen;

import java.util.Set;

final class Util {

    static boolean hasNoPay5Extractions(final Set<String> extractionNames) {
        for (String extractionName : extractionNames) {
            if (isPay5Extraction(extractionName)) {
                return false;
            }
        }
        return true;
    }

    static boolean isPay5Extraction(String extractionName) {
        return extractionName.equals("amountToPay") ||
                extractionName.equals("bic") ||
                extractionName.equals("iban") ||
                extractionName.equals("paymentReference") ||
                extractionName.equals("paymentRecipient");
    }
}
