package net.gini.android.vision.document;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;

import java.io.IOException;

/**
 * @exclude
 */
public final class DocumentFactory {

    @NonNull
    public static Document documentFromIntent(@NonNull final Intent intent,
            @NonNull final Context context) throws IOException {
        String mimeType = intent.getType();
        if ("application/pdf".equals(mimeType)) {
            PdfDocument.fromIntent(intent, context);
        }
        if ("image/".startsWith(mimeType)) {
            return ImageDocument.fromIntent(intent, context);
        }
        throw new IllegalArgumentException("Could not read data from intent Uri.");
    }
}
