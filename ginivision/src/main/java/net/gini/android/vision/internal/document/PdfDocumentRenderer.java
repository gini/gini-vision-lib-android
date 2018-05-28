package net.gini.android.vision.internal.document;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import net.gini.android.vision.document.PdfDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.pdf.Pdf;
import net.gini.android.vision.internal.util.Size;

/**
 * @exclude
 */
class PdfDocumentRenderer implements DocumentRenderer {

    private final PdfDocument mPdfDocument;
    private Pdf mPdf;
    private Bitmap mBitmap;
    private int mPageCount = -1;

    PdfDocumentRenderer(@NonNull final PdfDocument document) {
        mPdfDocument = document;
    }

    @Override
    public void toBitmap(@NonNull final Context context, @NonNull final Size targetSize,
            @NonNull final Callback callback) {
        final Pdf pdf = getPdf();
        if (mBitmap == null) {
            pdf.toBitmap(targetSize, context, new AsyncCallback<Bitmap>() {
                @Override
                public void onSuccess(final Bitmap result) {
                    mBitmap = result;
                    callback.onBitmapReady(result, 0);
                }

                @Override
                public void onError(final Exception exception) {
                    callback.onBitmapReady(null, 0);
                }
            });
        } else {
            callback.onBitmapReady(mBitmap, 0);
        }
    }

    private Pdf getPdf() {
        if (mPdf != null) {
            return mPdf;
        }
        mPdf = Pdf.fromDocument(mPdfDocument);
        return mPdf;
    }

    @Override
    public void getPageCount(@NonNull final Context context, @NonNull final AsyncCallback<Integer> asyncCallback) {
        final Pdf pdf = getPdf();
        if (mPageCount == -1) {
            pdf.getPageCount(context, new AsyncCallback<Integer>() {
                @Override
                public void onSuccess(final Integer result) {
                    mPageCount = result;
                    asyncCallback.onSuccess(result);
                }

                @Override
                public void onError(final Exception exception) {
                    asyncCallback.onError(exception);
                }
            });
        } else {
            asyncCallback.onSuccess(mPageCount);
        }
    }
}
