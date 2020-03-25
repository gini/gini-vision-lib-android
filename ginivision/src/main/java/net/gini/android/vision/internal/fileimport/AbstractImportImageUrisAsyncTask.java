package net.gini.android.vision.internal.fileimport;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;

import net.gini.android.vision.AsyncCallback;
import net.gini.android.vision.Document;
import net.gini.android.vision.GiniVision;
import net.gini.android.vision.ImportedFileValidationException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import androidx.annotation.NonNull;

/**
 * Created by Alpar Szotyori on 25.05.2018.
 *
 * Copyright (c) 2018 Gini GmbH.
 */

/**
 * Internal use only.
 *
 * @suppress
 */
public abstract class AbstractImportImageUrisAsyncTask extends
        AsyncTask<Uri, Integer, ImageMultiPageDocument> {

    private static final Logger LOG = LoggerFactory.getLogger(
            AbstractImportImageUrisAsyncTask.class);

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final Intent mIntent;
    private final AsyncCallback<ImageMultiPageDocument, ImportedFileValidationException> mCallback;
    private final GiniVision mGiniVision;
    private final Document.Source mSource;
    private final Document.ImportMethod mImportMethod;

    protected AbstractImportImageUrisAsyncTask(@NonNull final Context context,
            @NonNull final Intent intent,
            @NonNull final GiniVision giniVision,
            @NonNull final Document.Source source,
            @NonNull final Document.ImportMethod importMethod,
            @NonNull final AsyncCallback<ImageMultiPageDocument, ImportedFileValidationException>
                    callback) {
        mContext = context;
        mIntent = intent;
        mGiniVision = giniVision;
        mSource = source;
        mImportMethod = importMethod;
        mCallback = callback;
    }

    @Override
    protected ImageMultiPageDocument doInBackground(final Uri... uris) {
        LOG.debug("Importing uris from source {} and with import method {}", mSource,
                mImportMethod);
        final ImageMultiPageDocument multiPageDocument = new ImageMultiPageDocument(mSource,
                mImportMethod);
        FileImportValidator fileImportValidator = new FileImportValidator(mContext);
        if (!fileImportValidator.matchesCriteria(uris)) {
            onHaltingError(new ImportedFileValidationException(fileImportValidator.getError()));
            return null;
        }
        for (final Uri uri : uris) {
            LOG.debug("Importing from uri {}", uri);
            if (isCancelled()) {
                LOG.debug("Import cancelled for uri {}", uri);
                return null;
            }
            if (!UriHelper.isUriInputStreamAvailable(uri, mContext)) {
                LOG.error("Uri input stream not available for uri {}", uri);
                if (shouldHaltOnError(multiPageDocument, new ImportedFileValidationException( // NOPMD
                        "InputStream not available for one of the Intent's data Uris"))) {
                    LOG.debug("Halt on error for uri {}", uri);
                    return null;
                }
                continue;
            }
            fileImportValidator = new FileImportValidator(mContext); // NOPMD
            if (fileImportValidator.matchesCriteria(uri)) {
                if (isCancelled()) {
                    LOG.debug("Import cancelled for uri {}", uri);
                    return null;
                }
                if (isImage(uri)) {
                    final ImageDocument imageDocument = processImageUri(uri, multiPageDocument);
                    if (imageDocument == null) {
                        // Stop, because no document means processing has to be abandoned
                        // (cancellation or stopping on an error was requested)
                        return null;
                    }
                }
            } else {
                LOG.error("File validation failed for uri {} with error {}", uri,
                        fileImportValidator.getError());
                if (shouldHaltOnError(multiPageDocument,
                        new ImportedFileValidationException(fileImportValidator.getError()))) { // NOPMD
                    LOG.debug("Halt on error for uri {}", uri);
                    return null;
                }
            }
        }
        if (isCancelled()) {
            LOG.debug("Import cancelled");
            return null;
        }
        if (multiPageDocument.getDocuments().isEmpty()) {
            LOG.error("No image uris found");
            onError(multiPageDocument,
                    new ImportedFileValidationException("Intent did not contain images"));
        }
        LOG.debug("Finished importing uris from source {} and with import method {}", mSource,
                mImportMethod);
        return multiPageDocument;
    }

    private ImageDocument processImageUri(@NonNull final Uri uri,
            @NonNull final ImageMultiPageDocument multiPageDocument) {
        final ImageDocument document = createDocument(uri);
        LOG.debug("ImageDocument created from uri {}", uri);
        // Load uri into memory
        try {
            LOG.debug("Read uri into memory {}", uri);
            final byte[] bytesFromUri = UriHelper.getBytesFromUri(uri, mContext);
            document.setData(bytesFromUri);
        } catch (final IOException e) {
            LOG.error("Failed to read uri into memory {}", uri);
            if (shouldHaltOnError(multiPageDocument,
                    new ImportedFileValidationException(
                            "Failed to read file into memory"))) {
                LOG.debug("Halt on error for uri {}", uri);
                return null;
            }
            return document;
        }
        if (isCancelled()) {
            LOG.debug("Import cancelled for uri {}", uri);
            return null;
        }
        // Create Photo
        LOG.debug("Create Photo from uri {}", uri);
        final Photo photo = PhotoFactory.newPhotoFromDocument(document);
        if (isCancelled()) {
            LOG.debug("Import cancelled for uri {}", uri);
            return null;
        }
        // Compress Photo
        LOG.debug("Compress Photo created from uri {}", uri);
        photo.edit().compressByDefault().apply();
        if (isCancelled()) {
            LOG.debug("Import cancelled for uri {}", uri);
            return null;
        }
        // Save to local storage
        LOG.debug("Save compressed Photo to local storage created from uri {}", uri);
        final Uri localUri = mGiniVision.internal().getImageDiskStore()
                .save(mContext, photo.getData());
        if (localUri == null) {
            LOG.error("Failed to copy to app storage uri {}", uri);
            if (shouldHaltOnError(multiPageDocument,
                    new ImportedFileValidationException(
                            "Failed to copy to app storage"))) {
                LOG.debug("Halt on error for uri {}", uri);
                return null;
            }
            return document;
        }
        if (isCancelled()) {
            LOG.debug("Import cancelled for uri {}", uri);
            return null;
        }
        // Create compressed Document
        final ImageDocument compressedDocument =
                DocumentFactory.newImageDocumentFromPhoto(photo, localUri);
        LOG.debug("Compressed ImageDocument created from uri {}", uri);
        multiPageDocument.addDocument(compressedDocument);
        return compressedDocument;
    }

    @NonNull
    private ImageDocument createDocument(final Uri uri) {
        final String deviceOrientation = DeviceHelper.getDeviceOrientation(
                mContext);
        final String deviceType = DeviceHelper.getDeviceType(mContext);
        return DocumentFactory.newImageDocumentFromUri(uri,
                mIntent, mContext, deviceOrientation,
                deviceType, mImportMethod);
    }

    private boolean isImage(final Uri uri) {
        return IntentHelper.hasMimeTypeWithPrefix(uri, mContext, MimeType.IMAGE_PREFIX.asString());
    }

    protected abstract void onHaltingError(
            @NonNull final ImportedFileValidationException exception);

    protected abstract void onError(@NonNull ImageMultiPageDocument multiPageDocument,
            @NonNull final ImportedFileValidationException exception);

    protected abstract boolean shouldHaltOnError(@NonNull ImageMultiPageDocument multiPageDocument,
            @NonNull final ImportedFileValidationException exception);

    @NonNull
    public Document.Source getSource() {
        return mSource;
    }

    @NonNull
    public Document.ImportMethod getImportMethod() {
        return mImportMethod;
    }

    @NonNull
    public Context getContext() {
        return mContext;
    }

    @NonNull
    public AsyncCallback<ImageMultiPageDocument, ImportedFileValidationException> getCallback() {
        return mCallback;
    }
}
