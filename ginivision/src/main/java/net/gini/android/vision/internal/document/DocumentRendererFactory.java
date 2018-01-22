package net.gini.android.vision.internal.document;

import android.content.Context;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.PdfDocument;

/**
 * @exclude
 */

public final class DocumentRendererFactory {

    public static DocumentRenderer fromDocument(@NonNull final Document document,
            @NonNull final Context context) {
        switch (document.getType()) {
            case IMAGE:
                return new ImageDocumentRenderer((ImageDocument) document);
            case PDF:
                return new PdfDocumentRenderer((PdfDocument) document, context);
            default:
                throw new IllegalArgumentException("Unknown document type");
        }
    }

    private DocumentRendererFactory() {
    }
}
