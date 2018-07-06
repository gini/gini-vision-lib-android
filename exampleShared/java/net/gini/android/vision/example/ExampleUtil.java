package net.gini.android.vision.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.network.model.GiniVisionSpecificExtraction;

import java.util.Map;
import java.util.Set;

public final class ExampleUtil {

    public static boolean isIntentActionViewOrSend(@NonNull final Intent intent) {
        final String action = intent.getAction();
        return Intent.ACTION_VIEW.equals(action)
                || Intent.ACTION_SEND.equals(action)
                || Intent.ACTION_SEND_MULTIPLE.equals(action);
    }

    public static boolean hasNoPay5Extractions(final Set<String> extractionNames) {
        for (final String extractionName : extractionNames) {
            if (isPay5Extraction(extractionName)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPay5Extraction(final String extractionName) {
        return extractionName.equals("amountToPay") ||
                extractionName.equals("bic") ||
                extractionName.equals("iban") ||
                extractionName.equals("paymentReference") ||
                extractionName.equals("paymentRecipient");
    }

    public static Bundle getExtractionsBundle(
            @Nullable final Map<String, GiniVisionSpecificExtraction> extractions) {
        if (extractions == null) {
            return null;
        }
        final Bundle extractionsBundle = new Bundle();
        for (final Map.Entry<String, GiniVisionSpecificExtraction> entry : extractions.entrySet()) {
            extractionsBundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return extractionsBundle;
    }

    public static Bundle getLegacyExtractionsBundle(
            @Nullable final Map<String, SpecificExtraction> extractions) {
        if (extractions == null) {
            return null;
        }
        final Bundle extractionsBundle = new Bundle();
        for (final Map.Entry<String, SpecificExtraction> entry : extractions.entrySet()) {
            extractionsBundle.putParcelable(entry.getKey(), entry.getValue());
        }
        return extractionsBundle;
    }

    private ExampleUtil() {
    }
}
