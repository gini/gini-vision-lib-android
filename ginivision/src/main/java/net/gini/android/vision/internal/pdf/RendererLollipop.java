package net.gini.android.vision.internal.pdf;

import static net.gini.android.vision.internal.pdf.Pdf.DEFAULT_PREVIEW_HEIGHT;
import static net.gini.android.vision.internal.pdf.Pdf.DEFAULT_PREVIEW_WIDTH;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @exclude
 */
@RequiresApi(21)
class RendererLollipop implements Renderer {

    private static final Logger LOG = LoggerFactory.getLogger(RendererLollipop.class);

    private final Uri mUri;
    private Context mContext;

    RendererLollipop(@NonNull final Uri uri, @NonNull Context context) {
        mUri = uri;
        mContext = context;
    }

    @Nullable
    private Bitmap toBitmap(@NonNull final Size targetSize) {
        Bitmap bitmap = null;
        final PdfRenderer pdfRenderer = getPdfRenderer();
        if (pdfRenderer == null) {
            return null;
        }
        if (pdfRenderer.getPageCount() > 0) {
            PdfRenderer.Page page = pdfRenderer.openPage(0);
            Size optimalSize = calculateOptimalRenderingSize(page, targetSize);
            bitmap = createWhiteBitmap(optimalSize);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            page.close();
        }
        pdfRenderer.close();
        return bitmap;
    }

    @Nullable
    private PdfRenderer getPdfRenderer() {
        final ContentResolver contentResolver = mContext.getContentResolver();
        ParcelFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = contentResolver.openFileDescriptor(mUri, "r");
        } catch (FileNotFoundException e) {
            LOG.error("Pdf not found", e);
        }
        if (fileDescriptor == null) {
            return null;
        }
        try {
            return new PdfRenderer(fileDescriptor);
        } catch (IOException e) {
            LOG.error("Could not read pdf", e);
        }
        return null;
    }

    @Override
    public void toBitmap(@NonNull final Size targetSize,
            @NonNull final Callback callback) {
        final AsyncTask<RendererLollipop, Void, Bitmap> asyncTask =
                new AsyncTask<RendererLollipop, Void, Bitmap>() {
                    @Override
                    protected Bitmap doInBackground(final RendererLollipop... renderers) {
                        return renderers[0].toBitmap(targetSize);
                    }

                    @Override
                    protected void onPostExecute(final Bitmap bitmap) {
                        callback.onBitmapReady(bitmap);
                    }
                };
        asyncTask.execute(this);
    }

    @Override
    public int getPageCount() {
        final PdfRenderer pdfRenderer = getPdfRenderer();
        if (pdfRenderer == null) {
            return 0;
        }
        final int pageCount = pdfRenderer.getPageCount();
        pdfRenderer.close();
        return pageCount;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    private static Size calculateOptimalRenderingSize(@NonNull PdfRenderer.Page page,
            @NonNull Size previewSize) {
        previewSize = getDefaultPreviewSizeIfEmpty(previewSize);
        final float pageRatio = (float) page.getWidth() / (float) page.getHeight();
        final float previewRatio = (float) previewSize.width / (float) previewSize.height;
        if (pageRatio < previewRatio) {
            // The PDF page is taller than wide, or at least more so than the preview => fit the
            // height of the pdf page
            // to the preview and resize the width according to the pdf page's aspect ratio
            int height = previewSize.height;
            int width = (int) ((float) height * pageRatio);
            return new Size(width, height);
        } else {
            // The PDF page is wider than tall, or at least more so than the preview => fit the
            // width of the pdf page
            // to the preview and resize the height according to the pdf page's aspect ratio
            int width = previewSize.width;
            int height = (int) ((float) width / pageRatio);
            return new Size(width, height);
        }
    }

    @NonNull
    private static Bitmap createWhiteBitmap(@NonNull Size renderingSize) {
        final Bitmap bitmap = Bitmap.createBitmap(renderingSize.width, renderingSize.height,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        return bitmap;
    }

    @NonNull
    private static Size getDefaultPreviewSizeIfEmpty(@NonNull Size size) {
        if (size.width == 0 || size.height == 0) {
            return new Size(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT);
        }
        return size;
    }
}
