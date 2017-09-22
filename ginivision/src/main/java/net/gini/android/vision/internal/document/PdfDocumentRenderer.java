package net.gini.android.vision.internal.document;

import android.content.Context;
import android.support.annotation.NonNull;

import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.internal.pdf.Pdf;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
class PdfDocumentRenderer implements DocumentRenderer {

    private final PdfDocument mPdfDocument;
    private final Context mContext;
    private Pdf mPdf;

    PdfDocumentRenderer(@NonNull final PdfDocument document,
            @NonNull final Context context) {
        mPdfDocument = document;
        mContext = context;
    }

    @Override
    public void toBitmap(@NonNull final Size targetSize,
            @NonNull final Callback callback) {
        if (mPdf == null) {
            mPdf = Pdf.fromDocument(mPdfDocument);
        }
        callback.onBitmapReady(mPdf.toBitmap(targetSize, mContext), 0);
    }
}
