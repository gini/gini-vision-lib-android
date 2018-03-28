package net.gini.android.vision.camera;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageDocument.ImportMethod;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.storage.ImageDiskStore;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Alpar Szotyori on 23.03.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

class ImportUrisAsyncTask extends AsyncTask<List<Uri>, Void, ImageMultiPageDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(ImportUrisAsyncTask.class);

    private final Context mContext;
    private final Intent mIntent;
    private final AsyncCallback<ImageMultiPageDocument> mListener;
    private final ImageDiskStore mImageDiskStore;

    ImportUrisAsyncTask(@NonNull final Context context,
            @NonNull final Intent intent,
            @NonNull final ImageDiskStore imageDiskStore,
            @NonNull final AsyncCallback<ImageMultiPageDocument> listener) {
        mContext = context;
        mIntent = intent;
        mImageDiskStore = imageDiskStore;
        mListener = listener;
    }

    @Override
    protected ImageMultiPageDocument doInBackground(final List<Uri>[] urisList) {
        final List<Uri> uris = urisList[0];
        final ImageMultiPageDocument multiPageDocument = new ImageMultiPageDocument(true);
        for (final Uri uri : uris) {
            if (isCancelled()) {
                return multiPageDocument;
            }
            if (!UriHelper.isUriInputStreamAvailable(uri, mContext)) {
                LOG.error("Document import failed: InputStream not available for the Uri");
                addMultiPageDocumentError(mContext.getString(
                        R.string.gv_document_import_invalid_document), multiPageDocument);
                break;
            }
            final FileImportValidator fileImportValidator = new FileImportValidator(mContext);
            if (fileImportValidator.matchesCriteria(uri)) {
                if (IntentHelper.hasMimeTypeWithPrefix(uri, mContext,
                        IntentHelper.MimeType.IMAGE_PREFIX.asString())) {
                    try {
                        final Uri localUri = mImageDiskStore.save(mContext, uri);
                        if (localUri == null) {
                            LOG.error("Failed to import selected document: "
                                    + "could not copy to app storage");
                            addMultiPageDocumentError(mContext.getString(
                                    R.string.gv_document_import_invalid_document),
                                    multiPageDocument);
                            break;
                        }
                        final ImageDocument document = DocumentFactory.newImageDocumentFromUri(
                                localUri,
                                mIntent, mContext, DeviceHelper.getDeviceOrientation(mContext),
                                DeviceHelper.getDeviceType(mContext), ImportMethod.OPEN_WITH);
                        multiPageDocument.addDocument(document);
                    } catch (final IllegalArgumentException e) {
                        LOG.error("Failed to import selected document", e);
                        addMultiPageDocumentError(mContext.getString(
                                R.string.gv_document_import_invalid_document), multiPageDocument);
                    }
                }
            } else {
                String errorMessage = mContext.getString(
                        R.string.gv_document_import_invalid_document);
                final FileImportValidator.Error error = fileImportValidator.getError();
                if (error != null) {
                    errorMessage = mContext.getString(error.getTextResource());
                }
                addMultiPageDocumentError(errorMessage, multiPageDocument);
            }
        }
        return multiPageDocument;
    }

    @Override
    protected void onPostExecute(final ImageMultiPageDocument multiPageDocument) {
        mListener.onSuccess(multiPageDocument);
    }

    private void addMultiPageDocumentError(@NonNull final String string,
            @NonNull final ImageMultiPageDocument multiPageDocument) {
        final ImageDocument document = DocumentFactory.newEmptyImageDocument();
        multiPageDocument.addDocument(document);
        final GiniVisionDocumentError documentError = new GiniVisionDocumentError(string);
        multiPageDocument.addErrorForDocument(document, documentError);
    }
}
