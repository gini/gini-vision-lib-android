package net.gini.android.vision.camera;

import android.content.Context;
import android.content.Intent;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.ImportedFileValidationException;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.fileimport.AbstractImportImageUrisAsyncTask;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 23.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

class ImportImageDocumentUrisAsyncTask extends AbstractImportImageUrisAsyncTask {

    private ImportedFileValidationException mException;

    ImportImageDocumentUrisAsyncTask(@NonNull final Context context,
            @NonNull final Intent intent, @NonNull final GiniVision giniVision,
            @NonNull final Document.Source source,
            @NonNull final Document.ImportMethod importMethod,
            @NonNull final AsyncCallback<ImageMultiPageDocument, ImportedFileValidationException>
                    callback) {
        super(context, intent, giniVision, source, importMethod, callback);
    }

    @Override
    protected void onHaltingError(@NonNull final ImportedFileValidationException exception) {
        mException = exception;
    }

    @Override
    protected void onPostExecute(final ImageMultiPageDocument multiPageDocument) {
        if (multiPageDocument != null) {
            getCallback().onSuccess(multiPageDocument);
        } else if (mException != null) {
            getCallback().onError(mException);
        } else {
            getCallback().onCancelled();
        }
    }

    @Override
    protected void onError(@NonNull final ImageMultiPageDocument multiPageDocument,
            @NonNull final ImportedFileValidationException exception) {
        addMultiPageDocumentError(getContext().getString(
                R.string.gv_document_import_invalid_document), multiPageDocument);
    }

    @Override
    protected boolean shouldHaltOnError(@NonNull final ImageMultiPageDocument multiPageDocument,
            @NonNull final ImportedFileValidationException exception) {
        addMultiPageDocumentError(getContext().getString(
                R.string.gv_document_import_invalid_document), multiPageDocument);
        return false;
    }

    private void addMultiPageDocumentError(@NonNull final String string,
            @NonNull final ImageMultiPageDocument multiPageDocument) {
        final ImageDocument document = DocumentFactory.newEmptyImageDocument(getSource(),
                getImportMethod());
        multiPageDocument.addDocument(document);
        final GiniVisionDocumentError documentError = new GiniVisionDocumentError(string,
                GiniVisionDocumentError.ErrorCode.FILE_VALIDATION_FAILED);
        multiPageDocument.setErrorForDocument(document, documentError);
    }
}
