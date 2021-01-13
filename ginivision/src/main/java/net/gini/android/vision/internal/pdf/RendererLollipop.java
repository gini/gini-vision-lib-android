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

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.internal.util.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;

/**
 * Internal use only.
 *
 * This class is not thread safe due to the underlying {@link PdfRenderer}.
 *
 * @suppress
 */
@RequiresApi(21)
class RendererLollipop implements Renderer {

    private static final Logger LOG = LoggerFactory.getLogger(RendererLollipop.class);

    private final Uri mUri;
    private final Context mContext;

    RendererLollipop(@NonNull final Uri uri, @NonNull final Context context) {
        mUri = uri;
        mContext = context;
    }

    @Nullable
    @VisibleForTesting
    protected synchronized Bitmap toBitmap(@NonNull final Size targetSize) {
        final PdfRendererHelper pdfRendererHelper = new PdfRendererHelper(mContext, mUri);
        try {
            final PdfRenderer pdfRenderer = pdfRendererHelper.createPdfRenderer();
            if (pdfRenderer == null) {
                return null;
            }
            if (pdfRenderer.getPageCount() > 0) {
                final PdfRenderer.Page page = pdfRenderer.openPage(0);
                final Size optimalSize = calculateOptimalRenderingSize(page, targetSize);
                final Bitmap bitmap = createWhiteBitmap(optimalSize);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
                return bitmap;
            }
        } catch (final IOException | SecurityException e) {
            LOG.error("Could not read pdf", e);
        } finally {
            pdfRendererHelper.closePdfRenderer();
        }
        return null;
    }

    @Override
    public void toBitmap(@NonNull final Size targetSize,
            @NonNull final AsyncCallback<Bitmap, Exception> asyncCallback) {
        final RenderAsyncTask asyncTask = new RenderAsyncTask(this,
                targetSize,
                new AsyncCallback<Bitmap, Exception>() {
                    @Override
                    public void onSuccess(final Bitmap result) {
                        asyncCallback.onSuccess(result);
                    }

                    @Override
                    public void onError(final Exception exception) {
                        asyncCallback.onError(exception);
                    }

                    @Override
                    public void onCancelled() {
                        asyncCallback.onCancelled();
                    }
                });
        asyncTask.execute();
    }

    @Override
    public void getPageCount(@NonNull final AsyncCallback<Integer, Exception> asyncCallback) {
        final PageCountAsyncTask asyncTask = new PageCountAsyncTask(this,
                new AsyncCallback<Integer, Exception>() {
                    @Override
                    public void onSuccess(final Integer result) {
                        asyncCallback.onSuccess(result);
                    }

                    @Override
                    public void onError(final Exception exception) {
                        asyncCallback.onError(exception);
                    }

                    @Override
                    public void onCancelled() {
                        asyncCallback.onCancelled();
                    }
                });
        asyncTask.execute();
    }

    @Override
    public synchronized int getPageCount() {
        final PdfRendererHelper pdfRendererHelper = new PdfRendererHelper(mContext, mUri);
        try {
            final PdfRenderer pdfRenderer = pdfRendererHelper.createPdfRenderer();
            if (pdfRenderer == null) {
                return 0;
            }
            return pdfRenderer.getPageCount();
        } catch (final IOException | SecurityException e) {
            LOG.error("Could not read pdf", e);
        } finally {
            pdfRendererHelper.closePdfRenderer();
        }
        return 0;
    }

    @Override
    public boolean isPdfPasswordProtected() {
        final PdfRendererHelper pdfRendererHelper = new PdfRendererHelper(mContext, mUri);
        try {
            pdfRendererHelper.createPdfRenderer();
            return false;
        } catch (final IOException e) {
            LOG.error("Could not read pdf", e);
        } catch (final SecurityException e) {
            LOG.error("Could not read pdf", e);
            return true;
        } finally {
            pdfRendererHelper.closePdfRenderer();
        }
        return false;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    private static Size calculateOptimalRenderingSize(@NonNull final PdfRenderer.Page page,
            @NonNull final Size previewSize) {
        final Size newPreviewSize = getDefaultPreviewSizeIfEmpty(previewSize);
        final float pageRatio = (float) page.getWidth() / (float) page.getHeight();
        final float previewRatio = (float) newPreviewSize.width / (float) newPreviewSize.height;
        if (pageRatio < previewRatio) {
            // The PDF page is taller than wide, or at least more so than the preview => fit the
            // height of the pdf page
            // to the preview and resize the width according to the pdf page's aspect ratio
            final int height = newPreviewSize.height;
            final int width = (int) ((float) height * pageRatio);
            return new Size(width, height);
        } else {
            // The PDF page is wider than tall, or at least more so than the preview => fit the
            // width of the pdf page
            // to the preview and resize the height according to the pdf page's aspect ratio
            final int width = newPreviewSize.width;
            final int height = (int) ((float) width / pageRatio);
            return new Size(width, height);
        }
    }

    @NonNull
    private static Bitmap createWhiteBitmap(@NonNull final Size renderingSize) {
        final Bitmap bitmap = Bitmap.createBitmap(renderingSize.width, renderingSize.height,
                Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        return bitmap;
    }

    @NonNull
    private static Size getDefaultPreviewSizeIfEmpty(@NonNull final Size size) {
        if (size.width == 0 || size.height == 0) {
            return new Size(DEFAULT_PREVIEW_WIDTH, DEFAULT_PREVIEW_HEIGHT);
        }
        return size;
    }

    private static class RenderAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        private final RendererLollipop mRendererLollipop;
        private final Size mTargetSize;
        private final AsyncCallback<Bitmap, Exception> mCallback;

        private RenderAsyncTask(final RendererLollipop rendererLollipop,
                final Size targetSize,
                final AsyncCallback<Bitmap, Exception> callback) {
            mRendererLollipop = rendererLollipop;
            mTargetSize = targetSize;
            mCallback = callback;
        }

        @Override
        protected Bitmap doInBackground(final Void... voids) {
            return mRendererLollipop.toBitmap(mTargetSize);
        }

        @Override
        protected void onPostExecute(final Bitmap bitmap) {
            super.onPostExecute(bitmap);
            mCallback.onSuccess(bitmap);
        }
    }

    private static class PageCountAsyncTask extends AsyncTask<Void, Void, Integer> {

        private final RendererLollipop mRendererLollipop;
        private final AsyncCallback<Integer, Exception> mCallback;

        private PageCountAsyncTask(final RendererLollipop rendererLollipop,
                final AsyncCallback<Integer, Exception> callback) {
            mRendererLollipop = rendererLollipop;
            mCallback = callback;
        }

        @Override
        protected Integer doInBackground(final Void... voids) {
            return mRendererLollipop.getPageCount();
        }

        @Override
        protected void onPostExecute(final Integer pageCount) {
            super.onPostExecute(pageCount);
            mCallback.onSuccess(pageCount);
        }
    }

    /**
     * Helper for creating a {@link PdfRenderer} instance and to free up resources afterwads.
     */
    private static class PdfRendererHelper {

        private final Context mContext;
        private final Uri mUri;
        private ParcelFileDescriptor mFileDescriptor;
        private PdfRenderer mPdfRenderer;

        PdfRendererHelper(@NonNull final Context context, @NonNull final Uri uri) {
            mContext = context;
            mUri = uri;
        }

        @Nullable
        PdfRenderer createPdfRenderer() throws IOException, SecurityException {
            final ContentResolver contentResolver = mContext.getContentResolver();
            mFileDescriptor = contentResolver.openFileDescriptor(mUri, "r");
            if (mFileDescriptor == null) {
                return null;
            }
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
            return mPdfRenderer;
        }

        void closePdfRenderer() {
            if (mPdfRenderer != null) {
                mPdfRenderer.close();
            } else if (mFileDescriptor != null) {
                try {
                    mFileDescriptor.close();
                } catch (final IOException e) {
                    LOG.error("Could not close file descriptor", e);
                }
            }
        }
    }
}
