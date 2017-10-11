package net.gini.android.vision.internal.util;


import android.content.Context;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.pdf.Pdf;

import java.io.FileNotFoundException;

/**
 * @exclude
 */
public class FileImportValidator {

    public enum Error {
        TYPE_NOT_SUPPORTED(R.string.gv_document_import_error_type_not_supported),
        SIZE_TOO_LARGE(R.string.gv_document_import_error_size_too_large),
        TOO_MANY_PDF_PAGES(R.string.gv_document_import_error_too_many_pdf_pages);

        public int getTextResource() {
            return mTextResource;
        }

        private final int mTextResource;

        Error(@StringRes final int textResource) {
            mTextResource = textResource;
        }
    }

    private final Context mContext;
    private Error mError;

    public FileImportValidator(final Context context) {
        mContext = context;
    }

    @Nullable
    public Error getError() {
        return mError;
    }

    public boolean matchesCriteria(@NonNull final Uri fileUri) {
        final String type = mContext.getContentResolver().getType(fileUri);

        if (!isSupportedFileType(type)) {
            mError = Error.TYPE_NOT_SUPPORTED;
            return false;
        }

        if (!matchesSizeCriteria(fileUri)) {
            mError = Error.SIZE_TOO_LARGE;
            return false;
        }

        if (isPdf(type)) {
            if (!matchesPdfCriteria(fileUri)) {
                mError = Error.TOO_MANY_PDF_PAGES;
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
                || "image/gif".equals(type)
                || isPdf(type);
    }

    private boolean matchesPdfCriteria(final Uri fileUri) {
        final Pdf pdf = Pdf.fromUri(fileUri);
        final int pageCount = pdf.getPageCount(mContext);
        return pageCount <= 10;
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
