package net.gini.android.vision.internal.util;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.pdf.Pdf;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @exclude
 */
public class FileImportValidator {

    private static final int FILE_SIZE_LIMIT = 10485760; // 10MB
    private static final Logger LOG = LoggerFactory.getLogger(FileImportValidator.class);

    /**
     * File validation errors.
     */
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

    public boolean matchesCriteria(@NonNull final Intent intent, @NonNull final Uri fileUri) {
        final List<String> mimeTypes = IntentHelper.getMimeTypes(intent, mContext);
        return matchesCriteria(fileUri, mimeTypes);
    }

    public boolean matchesCriteria(@NonNull final Uri fileUri) {
        final List<String> mimeTypes = Collections.singletonList(
                IntentHelper.getMimeType(fileUri, mContext));
        return matchesCriteria(fileUri, mimeTypes);
    }

    private boolean matchesCriteria(final @NonNull Uri fileUri, final List<String> mimeTypes) {
        if (!isSupportedFileType(mimeTypes)) {
            mError = Error.TYPE_NOT_SUPPORTED;
            return false;
        }

        if (!matchesSizeCriteria(fileUri)) {
            mError = Error.SIZE_TOO_LARGE;
            return false;
        }

        if (isPdf(mimeTypes)) {
            if (!matchesPdfCriteria(fileUri)) { // NOPMD
                mError = Error.TOO_MANY_PDF_PAGES;
                return false;
            }
        }

        return true;
    }

    private boolean isPdf(final List<String> mimeTypes) {
        for (final String mimeType : mimeTypes) {
            if ("application/pdf".equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSupportedFileType(final List<String> mimeTypes) {
        if (isPdf(mimeTypes)) {
            return true;
        }
        for (final String mimeType : mimeTypes) {
            if ("image/jpeg".equals(mimeType)
                    || "image/png".equals(mimeType)
                    || "image/gif".equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesPdfCriteria(final Uri fileUri) {
        final Pdf pdf = Pdf.fromUri(fileUri);
        final int pageCount = pdf.getPageCount(mContext);
        return pageCount <= 10;
    }

    private boolean matchesSizeCriteria(final Uri fileUri) {
        try {
            final int fileSize = UriHelper.getFileSizeFromUri(fileUri, mContext);
            return fileSize < FILE_SIZE_LIMIT;
        } catch (final IllegalStateException e) {
            LOG.error("Could not retrieve file size for uri: ", fileUri, e);
        }
        return false;
    }
}
