package net.gini.android.vision.internal.document;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.internal.pdf.Pdf;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
public class PdfDocumentRenderer implements DocumentRenderer {

    private final Pdf mPdf;
    private final Context mContext;

    public PdfDocumentRenderer(@NonNull final PdfDocument document,
            @NonNull final Context context) {
        mPdf = Pdf.fromDocument(document);
        mContext = context;
    }

    @Nullable
    @Override
    public Bitmap toBitmap(@NonNull final Size targetSize) {
        return mPdf.toBitmap(targetSize, mContext);
    }

    @Override
    public int getRotationForDisplay() {
        return 0;
    }
}
