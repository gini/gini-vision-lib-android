package net.gini.android.vision.internal.util;


import android.content.Context;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import net.gini.android.vision.GiniVisionError;

import java.io.FileNotFoundException;
import java.io.IOException;

public class FileImportValidator {

    private final Context mContext;
    private GiniVisionError mError;

    public FileImportValidator(final Context context) {
        mContext = context;
    }

    @Nullable
    public GiniVisionError getError() {
        return mError;
    }

    public boolean matchesCriteria(@NonNull final Uri fileUri) {
        final String type = mContext.getContentResolver().getType(fileUri);

        if (!isSupportedFileType(type)) {
            mError = new GiniVisionError(GiniVisionError.ErrorCode.DOCUMENT_IMPORT,
                    "File type is not supported for document import.");
            return false;
        }

        if (!matchesSizeCriteria(fileUri)) {
            mError = new GiniVisionError(GiniVisionError.ErrorCode.DOCUMENT_IMPORT,
                    "File is too big for document import.");
            return false;
        }

        if (isPdf(type)) {
            if (!matchesPdfCriteria(fileUri)) {
                mError = new GiniVisionError(GiniVisionError.ErrorCode.DOCUMENT_IMPORT,
                        "PDF does not match the criteria for document import.");
                return false;
            }
        }

        return true;
    }

    private boolean isPdf(final String fileType) {
        return "application/pdf".equals(fileType);
    }

    private boolean isSupportedFileType(final String type) {
        return "image/jpeg".equals(type)
                || "image/png".equals(type)
                || "image/tiff".equals(type)
                || "image/gif".equals(type)
                || isPdf(type);
    }

    private boolean matchesPdfCriteria(final Uri fileUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                final ParcelFileDescriptor parcelFileDescriptor =
                        mContext.getContentResolver().openFileDescriptor(fileUri, "r");
                if (parcelFileDescriptor != null) {
                    PdfRenderer pdfRenderer = new PdfRenderer(parcelFileDescriptor);
                    final int pageCount = pdfRenderer.getPageCount();
                    if (pageCount <= 10) {
                        return true;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //not sure if we should just ignore this case
            return true;
        }
        return false;
    }

    private boolean matchesSizeCriteria(final Uri fileUri) {
        ParcelFileDescriptor parcelFileDescriptor = null;
        try {
            parcelFileDescriptor = mContext.getContentResolver().openFileDescriptor(fileUri, "r");
            if (parcelFileDescriptor != null) {
                return parcelFileDescriptor.getStatSize() < 10485760;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }
}
