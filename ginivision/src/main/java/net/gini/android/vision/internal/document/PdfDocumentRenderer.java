package net.gini.android.vision.internal.document;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.internal.pdf.Pdf;
import net.gini.android.vision.internal.pdf.Renderer;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
class PdfDocumentRenderer implements DocumentRenderer {

    private final PdfDocument mPdfDocument;
    private final Context mContext;
    private Pdf mPdf;
    private Bitmap mBitmap;

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
        if (mBitmap == null) {
            mPdf.toBitmap(targetSize, mContext, new Renderer.Callback() {
                @Override
                public void onBitmapReady(@Nullable final Bitmap bitmap) {
                    mBitmap = bitmap;
                    callback.onBitmapReady(bitmap, 0);
                }
            });
        } else {
            callback.onBitmapReady(mBitmap, 0);
        }
    }
}
