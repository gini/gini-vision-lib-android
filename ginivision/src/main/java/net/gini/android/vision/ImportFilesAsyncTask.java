package net.gini.android.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import java.io.IOException;

/**
 * Created by Alpar Szotyori on 22.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */
class ImportFilesAsyncTask extends AsyncTask<Uri, Void, ImageMultiPageDocument> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final Intent mIntent;
    private final GiniVision mGiniVision;
    private final GiniVisionFileImport.Callback<ImageMultiPageDocument> mCallback;
    private ImportedFileValidationException mException;

    ImportFilesAsyncTask(@NonNull final Context context,
            @NonNull final Intent intent,
            final GiniVision giniVision,
            @NonNull final GiniVisionFileImport.Callback<ImageMultiPageDocument> callback) {
        mContext = context;
        mIntent = intent;
        mGiniVision = giniVision;
        mCallback = callback;
    }

    @Override
    protected ImageMultiPageDocument doInBackground(final Uri... uris) {
        final ImageMultiPageDocument multiPageDocument = new ImageMultiPageDocument(
                Document.Source.newExternalSource(), Document.ImportMethod.OPEN_WITH);
        for (final Uri uri : uris) {
            if (isCancelled()) {
                return null;
            }
            if (!UriHelper.isUriInputStreamAvailable(uri, mContext)) {
                mException = new ImportedFileValidationException(
                        "InputStream not available for one of the Intent's data Uris");
                return null;
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
                            deviceType, Document.ImportMethod.OPEN_WITH);
                    try {
                        // Load uri into memory
                        final byte[] bytesFromUri = UriHelper.getBytesFromUri(uri, mContext);
                        document.setData(bytesFromUri);
                    } catch (final IOException e) {
                        mException = new ImportedFileValidationException(
                                "Failed to read file into memory");
                        return null;
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
                    final Uri localUri = mGiniVision.internal().getImageDiskStore()
                            .save(mContext, photo.getData());
                    if (localUri == null) {
                        mException = new ImportedFileValidationException(
                                "Failed to copy to app storage");
                        return null;
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
                mException = new ImportedFileValidationException(
                        fileImportValidator.getError());
                return null;
            }
        }
        if (multiPageDocument.getDocuments().isEmpty()) {
            mException = new ImportedFileValidationException("Intent did not contain images");
            return null;
        }
        if (isCancelled()) {
            return null;
        }
        return multiPageDocument;
    }

    @Override
    protected void onPostExecute(final ImageMultiPageDocument multiPageDocument) {
        if (multiPageDocument != null) {
            mCallback.onDone(multiPageDocument);
        } else if (mException != null) {
            mCallback.onFailed(mException);
        } else {
            mCallback.onCancelled();
        }
    }
}
