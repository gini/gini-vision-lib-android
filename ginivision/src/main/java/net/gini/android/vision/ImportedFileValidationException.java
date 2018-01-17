package net.gini.android.vision;

import android.support.annotation.Nullable;

import net.gini.android.vision.internal.util.FileImportValidator;

/**
 * Thrown when a file failed validation.
 * <p>
 * Call {@link ImportedFileValidationException#getValidationError()} to find out the failure reason.
 */
public class ImportedFileValidationException extends Exception {

    private final FileImportValidator.Error mError;

    ImportedFileValidationException(final FileImportValidator.Error error) {
        mError = error;
    }

    ImportedFileValidationException(final String message) {
        super(message);
        mError = null;
    }

    @Nullable
    public FileImportValidator.Error getValidationError() {
        return mError;
    }
}
