package net.gini.android.vision.screen;

import static net.gini.android.vision.example.ExampleUtil.getLegacyExtractionsBundle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import net.gini.android.models.SpecificExtraction;
import net.gini.android.vision.Document;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.document.QRCodeDocument;
import net.gini.android.vision.example.BaseExampleApp;
import net.gini.android.vision.example.DocumentAnalyzer;
import net.gini.android.vision.example.SingleDocumentAnalyzer;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class CameraScreenApiActivity extends CameraActivity {

    private static final Logger LOG = LoggerFactory.getLogger(CameraScreenApiActivity.class);

    // Set to true to allow execution of the custom code check
    private static final boolean DO_CUSTOM_DOCUMENT_CHECK = false;

    private SingleDocumentAnalyzer mSingleDocumentAnalyzer;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSingleDocumentAnalyzer = ((BaseExampleApp) getApplication()).getSingleDocumentAnalyzer();
    }

    @Override
    public void onCheckImportedDocument(@NonNull final Document document,
            @NonNull final DocumentCheckResultCallback callback) {
        // We can apply custom checks here to an imported document and notify the Gini Vision
        // Library about the result
        // IMPORTANT: do not call super as it will lead to unexpected behaviors

        // As an example we show how to allow only jpegs and pdfs smaller than 5MB
        if (DO_CUSTOM_DOCUMENT_CHECK) {
            // Use the Intent with which the document was imported to access its contents
            // (document.getData() may be null)
            final Intent intent = document.getIntent();
            if (intent == null) {
                callback.documentRejected(getString(R.string.gv_document_import_error));
                return;
            }
            final Uri uri = IntentHelper.getUri(intent);
            if (uri == null) {
                callback.documentRejected(getString(R.string.gv_document_import_error));
                return;
            }
            if (hasMoreThan5MB(uri)) {
                callback.documentRejected(getString(R.string.document_size_too_large));
                return;
            }
            // IMPORTANT: always call one of the callback methods
            if (isJpegOrPdf(uri)) {
                callback.documentAccepted();
            } else {
                callback.documentRejected(getString(R.string.unsupported_document_type));
            }
        } else {
            // IMPORTANT: always call one of the callback methods
            callback.documentAccepted();
        }
    }

    private boolean hasMoreThan5MB(final Uri uri) {
        final int fileSize = UriHelper.getFileSizeFromUri(uri, this);
        return fileSize > 5 * 1024 * 1024;
    }

    private boolean isJpegOrPdf(final Uri uri) {
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                final byte[] magicBytes = new byte[4];
                final int read = inputStream.read(magicBytes);
                inputStream.reset();
                return read != -1
                        && (isJpegWithExif(inputStream, magicBytes, read)
                        || isPDF(uri, magicBytes, read));
            }
        } catch (final FileNotFoundException e) {
            LOG.error("Could not open document", e);
            return false;
        } catch (final IOException e) {
            LOG.error("Could not read document", e);
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    private boolean isJpegWithExif(final InputStream inputStream, final byte[] magicBytes,
            final int read)
            throws IOException {
        return isDecodableToBitmap(inputStream)
                && read == 4
                && magicBytes[0] == (byte) 0xFF
                && magicBytes[1] == (byte) 0xD8
                && magicBytes[2] == (byte) 0xFF
                && magicBytes[3] == (byte) 0xE1;
    }

    private boolean isDecodableToBitmap(final InputStream inputStream)
            throws IOException {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        final Bitmap bitmap = BitmapFactory.decodeStream(inputStream, new Rect(), options);
        return bitmap != null;
    }

    private boolean isPDF(final Uri uri, final byte[] magicBytes, final int read) {
        boolean isRenderablePdf = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isRenderablePdf = isRenderablePDF(uri);
        }
        return isRenderablePdf
                && read == 4
                && magicBytes[0] == (byte) 0x25
                && magicBytes[1] == (byte) 0x50
                && magicBytes[2] == (byte) 0x44
                && magicBytes[3] == (byte) 0x46;
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isRenderablePDF(final Uri uri) {
        final ParcelFileDescriptor fileDescriptor;
        try {
            fileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        } catch (final FileNotFoundException e) {
            LOG.error("Pdf not found", e);
            return false;
        }
        if (fileDescriptor == null) {
            LOG.error("Pdf not found");
            return false;
        }
        try {
            final PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
            pdfRenderer.close();
            return true;
        } catch (final IOException e) {
            LOG.error("Could not read pdf", e);
        }
        return false;
    }

    @Override
    public void onQRCodeAvailable(@NonNull final QRCodeDocument qrCodeDocument) {
        showActivityIndicatorAndDisableInteraction();
        mSingleDocumentAnalyzer.cancelAnalysis();
        mSingleDocumentAnalyzer.analyzeDocument(qrCodeDocument,
                new DocumentAnalyzer.Listener() {
                    @Override
                    public void onException(final Exception exception) {
                        hideActivityIndicatorAndEnableInteraction();
                        showError(getString(R.string.qrcode_error), 4000);
                    }

                    @Override
                    public void onExtractionsReceived(
                            final Map<String, SpecificExtraction> extractions) {
                        hideActivityIndicatorAndEnableInteraction();
                        final Intent result = new Intent();
                        final Bundle extractionsBundle = getLegacyExtractionsBundle(extractions);
                        result.putExtra(MainActivity.EXTRA_OUT_EXTRACTIONS, extractionsBundle);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                });
    }
}
