package net.gini.android.vision.example;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import net.gini.android.models.Box;
import net.gini.android.models.Extraction;
import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.PaymentData;

import java.util.Collections;
import java.util.Set;

public final class ExampleUtil {

    public static boolean isIntentActionViewOrSend(@NonNull final Intent intent) {
        final String action = intent.getAction();
        return Intent.ACTION_VIEW.equals(action) || Intent.ACTION_SEND.equals(action);
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

    public static Bundle getExtractionsBundle(@NonNull final PaymentData paymentData) {
        final Bundle extractionsBundle = new Bundle();
        extractionsBundle.putParcelable("paymentReference",
                createSpecificExtraction("paymentReference",
                        paymentData.getPaymentReference()));
        extractionsBundle.putParcelable("paymentRecipient",
                createSpecificExtraction("paymentRecipient",
                        paymentData.getPaymentRecipient()));
        extractionsBundle.putParcelable("amountToPay",
                createSpecificExtraction("amountToPay",
                        paymentData.getAmount()));
        extractionsBundle.putParcelable("iban",
                createSpecificExtraction("iban",
                        paymentData.getIBAN()));
        extractionsBundle.putParcelable("bic",
                createSpecificExtraction("bic",
                        paymentData.getBIC()));
        return extractionsBundle;
    }

    @NonNull
    private static SpecificExtraction createSpecificExtraction(
            final @NonNull String name, final @NonNull String value) {
        return new SpecificExtraction(name, value, "", new Box(0, 0, 0, 0, 0),
                Collections.<Extraction>emptyList());
    }

    private ExampleUtil() {
    }
}
