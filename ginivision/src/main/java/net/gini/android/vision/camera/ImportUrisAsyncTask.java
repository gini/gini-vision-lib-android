package net.gini.android.vision.camera;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.Document;
import net.gini.android.vision.R;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.GiniVisionDocumentError;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.AsyncCallback;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.internal.storage.ImageDiskStore;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
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
    private final Document.Source mSource;
    private final Document.ImportMethod mImportMethod;

    ImportUrisAsyncTask(@NonNull final Context context,
            @NonNull final Intent intent,
            @NonNull final ImageDiskStore imageDiskStore,
            final Document.Source source,
            final Document.ImportMethod importMethod,
            @NonNull final AsyncCallback<ImageMultiPageDocument> listener) {
        mContext = context;
        mIntent = intent;
        mImageDiskStore = imageDiskStore;
        mSource = source;
        mImportMethod = importMethod;
        mListener = listener;
    }

    @Override
    protected ImageMultiPageDocument doInBackground(final List<Uri>[] urisList) {
        final List<Uri> uris = urisList[0];
        final ImageMultiPageDocument multiPageDocument = new ImageMultiPageDocument(
                mSource, mImportMethod);
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
                if (isCancelled()) {
                    return null;
                }
                if (IntentHelper.hasMimeTypeWithPrefix(uri, mContext,
                        MimeType.IMAGE_PREFIX.asString())) {
                    final String deviceOrientation = DeviceHelper.getDeviceOrientation(
                            mContext);
                    final String deviceType = DeviceHelper.getDeviceType(mContext);
                    // Create Document
                    final ImageDocument document = DocumentFactory.newImageDocumentFromUri(uri,
                            mIntent, mContext, deviceOrientation,
                            deviceType, Document.ImportMethod.PICKER);
                    try {
                        // Load uri into memory
                        final byte[] bytesFromUri = UriHelper.getBytesFromUri(uri, mContext);
                        document.setData(bytesFromUri);
                    } catch (final IOException e) {
                        LOG.error("Failed to import selected document: "
                                + "could not read file into memory");
                        addMultiPageDocumentError(mContext.getString(
                                R.string.gv_document_import_invalid_document),
                                multiPageDocument);
                        break;
                    }
                    if (isCancelled()) {
                        return null;
                    }
                    // Create Photo
                    final Photo photo = PhotoFactory.newPhotoFromDocument(document);
                    if (isCancelled()) {
                        return null;
                    }
                    // Compress Photo
                    photo.edit().compressByDefault().apply();
                    if (isCancelled()) {
                        return null;
                    }
                    // Save to local storage
                    final Uri localUri = mImageDiskStore.save(mContext, photo.getData());
                    if (localUri == null) {
                        LOG.error("Failed to import selected document: "
                                + "could not copy to app storage");
                        addMultiPageDocumentError(mContext.getString(
                                R.string.gv_document_import_invalid_document),
                                multiPageDocument);
                        break;
                    }
                    if (isCancelled()) {
                        return null;
                    }
                    // Create compressed Document
                    final ImageDocument compressedDocument =
                            DocumentFactory.newImageDocumentFromPhoto(photo, localUri);
                    multiPageDocument.addDocument(compressedDocument);
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
        final ImageDocument document = DocumentFactory.newEmptyImageDocument(mSource, mImportMethod);
        multiPageDocument.addDocument(document);
        final GiniVisionDocumentError documentError = new GiniVisionDocumentError(string,
                GiniVisionDocumentError.ErrorCode.FILE_VALIDATION_FAILED);
        multiPageDocument.setErrorForDocument(document, documentError);
    }
}
