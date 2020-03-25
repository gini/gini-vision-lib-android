package net.gini.android.vision.internal.util;


import static net.gini.android.vision.util.UriHelper.getMimeType;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.gini.android.vision.R;
import net.gini.android.vision.internal.pdf.Pdf;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Internal use only.
 *
 * @suppress
 */
public class FileImportValidator {

    private static final Logger LOG = LoggerFactory.getLogger(FileImportValidator.class);

    private static final int FILE_SIZE_LIMIT = 10485760; // 10MB
    private static final int PDF_PAGE_LIMIT = 10;
    /**
     * Internal use only.
     *
     * @suppress
     */
    public static final int DOCUMENT_PAGE_LIMIT = 10;


    /**
     * File validation errors.
     */
    public enum Error {
        TYPE_NOT_SUPPORTED(R.string.gv_document_import_error_type_not_supported),
        SIZE_TOO_LARGE(R.string.gv_document_import_error_size_too_large),
        TOO_MANY_PDF_PAGES(R.string.gv_document_import_error_too_many_pdf_pages),
        PASSWORD_PROTECTED_PDF(R.string.gv_document_import_error_password_protected_pdf),
        TOO_MANY_DOCUMENT_PAGES(R.string.gv_document_error_too_many_pages);

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
        final List<String> mimeTypes = Collections.singletonList(getMimeType(fileUri, mContext));
        return matchesCriteria(fileUri, mimeTypes);
    }

    public boolean matchesCriteria(@NonNull final Uri[] fileUris) {
        if (fileUris.length > DOCUMENT_PAGE_LIMIT) {
            mError = Error.TOO_MANY_DOCUMENT_PAGES;
            return false;
        }
        return true;
    }

    private boolean matchesCriteria(@NonNull final Uri fileUri, final List<String> mimeTypes) {
        if (!isSupportedFileType(mimeTypes)) {
            mError = Error.TYPE_NOT_SUPPORTED;
            return false;
        }

        if (!matchesSizeCriteria(fileUri)) {
            mError = Error.SIZE_TOO_LARGE;
            return false;
        }

        if (isPdf(mimeTypes)) {
            final Pdf pdf = Pdf.fromUri(fileUri);
            if (!matchesPdfPageCountCriteria(pdf)) { // NOPMD
                mError = Error.TOO_MANY_PDF_PAGES;
                return false;
            }
            if (!matchesPdfNoPasswordCriteria(pdf)) { // NOPMD
                mError = Error.PASSWORD_PROTECTED_PDF;
                return false;
            }
        }

        return true;
    }

    private boolean isPdf(final List<String> mimeTypes) {
        for (final String mimeType : mimeTypes) {
            if (MimeType.APPLICATION_PDF.equals(mimeType)) {
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
            if (MimeType.IMAGE_JPEG.equals(mimeType)
                    || MimeType.IMAGE_PNG.equals(mimeType)
                    || MimeType.IMAGE_GIF.equals(mimeType)) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesPdfPageCountCriteria(final Pdf pdf) {
        final int pageCount = pdf.getPageCount(mContext);
        return pageCount <= PDF_PAGE_LIMIT;
    }

    private boolean matchesPdfNoPasswordCriteria(final Pdf pdf) {
        return !pdf.isPasswordProtected(mContext);
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
