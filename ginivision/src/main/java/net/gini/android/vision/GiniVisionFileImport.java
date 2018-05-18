package net.gini.android.vision;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.camera.photo.Photo;
import net.gini.android.vision.internal.camera.photo.PhotoFactory;
import net.gini.android.vision.internal.util.ActivityHelper;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.review.ReviewActivity;
import net.gini.android.vision.review.multipage.MultiPageReviewActivity;
import net.gini.android.vision.util.CancellationToken;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

import java.io.IOException;
import java.util.List;

/**
 * This class contains methods for preparing launching the Gini Vision Library with a file received
 * from another app.
 */
public final class GiniVisionFileImport {

    /**
     * <b>Screen API</b>
     * <p>
     * When your application receives a file from another application you can use this method to
     * create an Intent for launching the Gini Vision Library.
     * <p>
     *     Start the Intent with {@link android.app.Activity#startActivityForResult(Intent,
     *     int)} to receive the {@link GiniVisionError} in case there was an error.
     * </p>
     *
     * @param intent                the Intent your app received
     * @param context               Android context
     * @param reviewActivityClass   the class of your application's {@link ReviewActivity} subclass
     * @param analysisActivityClass the class of your application's {@link AnalysisActivity}
     *                              subclass
     * @return an Intent for launching the Gini Vision Library
     * @throws ImportedFileValidationException if the file didn't pass validation
     * @throws IllegalArgumentException        if the Intent's data is not valid or the mime type is not
     *                                         supported
     */
    @NonNull
    public static Intent createIntentForImportedFile(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final Class<? extends ReviewActivity> reviewActivityClass,
            @NonNull final Class<? extends AnalysisActivity> analysisActivityClass)
            throws ImportedFileValidationException {
        final Document document = createDocumentForImportedFile(intent, context);
        final Intent giniVisionIntent;
        if (document.isReviewable()) {
            giniVisionIntent = createReviewActivityIntent(context, reviewActivityClass,
                    analysisActivityClass, document);
        } else {
            giniVisionIntent = new Intent(context, analysisActivityClass);
            giniVisionIntent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, document);
        }
        return giniVisionIntent;
    }

    @NonNull
    private static Intent createReviewActivityIntent(final @NonNull Context context,
            @NonNull final Class<? extends ReviewActivity> reviewActivityClass,
            @NonNull final Class<? extends AnalysisActivity> analysisActivityClass,
            final Document document) {
        final Intent giniVisionIntent;
        giniVisionIntent = new Intent(context, reviewActivityClass);
        giniVisionIntent.putExtra(ReviewActivity.EXTRA_IN_DOCUMENT, document);
        ActivityHelper.setActivityExtra(giniVisionIntent,
                ReviewActivity.EXTRA_IN_ANALYSIS_ACTIVITY, context, analysisActivityClass);
        return giniVisionIntent;
    }

    /**
     * <b>Component API</b>
     * <p>
     * When your application receives a file from another application you can use this method to
     * create a Document for launching one of the Gini Vision Library's Review Fragments or Analysis
     * Fragments.
     * <p>
     * If the Document can be reviewed ({@link Document#isReviewable()}) launch one of the Review
     * Fragments ({@link net.gini.android.vision.review.ReviewFragmentCompat} or {@link
     * net.gini.android.vision.review.ReviewFragmentStandard}).
     * <p>
     * If the Document cannot be reviewed you must launch one of the Analysis Fragments ({@link
     * net.gini.android.vision.analysis.AnalysisFragmentCompat} or {@link
     * net.gini.android.vision.analysis.AnalysisFragmentStandard}).
     *
     * @param intent  the Intent your app received
     * @param context Android context
     * @return a Document for launching one of the Gini Vision Library's Review Fragments or
     * Analysis Fragments
     * @throws ImportedFileValidationException if the file didn't pass validation
     */
    @NonNull
    public static Document createDocumentForImportedFile(@NonNull final Intent intent,
            @NonNull final Context context) throws ImportedFileValidationException {
        final Uri uri = IntentHelper.getUri(intent);
        if (uri == null) {
            throw new ImportedFileValidationException("Intent data did not contain a Uri");
        }
        if (!UriHelper.isUriInputStreamAvailable(uri, context)) {
            throw new ImportedFileValidationException(
                    "InputStream not available for Intent's data Uri");
        }
        final FileImportValidator fileImportValidator = new FileImportValidator(context);
        if (fileImportValidator.matchesCriteria(intent, uri)) {
            return DocumentFactory.newDocumentFromIntent(intent, context,
                    DeviceHelper.getDeviceOrientation(context), DeviceHelper.getDeviceType(context),
                    Document.ImportMethod.OPEN_WITH);
        } else {
            throw new ImportedFileValidationException(fileImportValidator.getError());
        }
    }

    /**
     * <b>Screen API</b>
     * <p>
     * When your application receives a file from another application you can use this method to
     * create an Intent for launching the Gini Vision Library.
     * <p>
     *     Start the Intent with {@link android.app.Activity#startActivityForResult(Intent,
     *     int)} to receive the extractions or a {@link GiniVisionError} in case there was an error.
     * </p>
     *
     * @param intent                the Intent your app received
     * @param context               Android context subclass
     * @throws ImportedFileValidationException if the file didn't pass validation
     */
    public static CancellationToken createIntentForImportedFiles(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final Callback<Intent> callback) {
        final CancellationToken cancellationToken =
                createDocumentForImportedFiles(intent, context, new Callback<Document>() {
                    @Override
                    public void onDone(@NonNull final Document result) {
                        final Intent giniVisionIntent = createIntent(result, context);
                        callback.onDone(giniVisionIntent);
                    }

                    @Override
                    public void onFailed(
                            @NonNull final ImportedFileValidationException exception) {
                        callback.onFailed(exception);
                    }

                    @Override
                    public void onCancelled() {
                        callback.onCancelled();
                    }
                });
        return new CancellationToken() {
            @Override
            public void cancel() {
                cancellationToken.cancel();
            }
        };
    }

    @NonNull
    private static Intent createIntent(final @NonNull Document result,
            final @NonNull Context context) {
        final Intent giniVisionIntent;
        if (result.getType() == Document.Type.IMAGE_MULTI_PAGE) {
            final ImageMultiPageDocument multiPageDocument = (ImageMultiPageDocument) result;
            final List<ImageDocument> imageDocuments = multiPageDocument.getDocuments();
            if (imageDocuments.size() > 1) {
                giniVisionIntent = MultiPageReviewActivity.createIntent(context);
            } else {
                final ImageDocument imageDocument = imageDocuments.get(0);
                giniVisionIntent = createReviewActivityIntent(context, ReviewActivity.class,
                        AnalysisActivity.class, imageDocument);
            }
        } else {
            if (result.isReviewable()) {
                giniVisionIntent = createReviewActivityIntent(context, ReviewActivity.class,
                        AnalysisActivity.class, result);
            } else {
                giniVisionIntent = new Intent(context, AnalysisActivity.class);
                giniVisionIntent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, result);
            }
        }
        return giniVisionIntent;
    }

    public static CancellationToken createDocumentForImportedFiles(@NonNull final Intent intent,
            @NonNull final Context context, @NonNull final Callback<Document> callback) {
        if (!GiniVision.hasInstance()) {
            callback.onFailed(new ImportedFileValidationException(
                    "Cannot import files. GiniVision instance not available. Create it with GiniVision.newInstance()."));
            return new CancellationToken() {
                @Override
                public void cancel() {
                }
            };
        }
        final List<Uri> uris = IntentHelper.getUris(intent);
        if (uris == null) {
            callback.onFailed(
                    new ImportedFileValidationException("Intent data did not contain Uris"));
            return new CancellationToken() {
                @Override
                public void cancel() {
                }
            };
        }
        final ImportFilesAsyncTask asyncTask = new ImportFilesAsyncTask(context, intent,
                new Callback<ImageMultiPageDocument>() {
                    @Override
                    public void onDone(@NonNull final ImageMultiPageDocument result) {
                        if (!GiniVision.hasInstance()) {
                            callback.onFailed(new ImportedFileValidationException(
                                    "Cannot import files. GiniVision instance not available. Create it with GiniVision.newInstance()."));
                            return;
                        }
                        GiniVision.getInstance().internal().getImageMultiPageDocumentMemoryStore()
                                .setMultiPageDocument(result);
                        callback.onDone(result);
                    }

                    @Override
                    public void onFailed(@NonNull final ImportedFileValidationException exception) {
                        callback.onFailed(exception);
                    }

                    @Override
                    public void onCancelled() {
                        callback.onCancelled();
                    }
                });
        asyncTask.execute(uris.toArray(new Uri[uris.size()]));
        return new CancellationToken() {
            @Override
            public void cancel() {
                asyncTask.cancel(false);
            }
        };
    }

    private GiniVisionFileImport() {
    }

    public interface Callback<T> {

        void onDone(@NonNull final T result);

        void onFailed(@NonNull final ImportedFileValidationException exception);

        void onCancelled();
    }

    private static class ImportFilesAsyncTask extends AsyncTask<Uri, Void, ImageMultiPageDocument> {

        @SuppressLint("StaticFieldLeak")
        private final Context mContext;
        private final Intent mIntent;
        private final Callback<ImageMultiPageDocument> mCallback;
        private ImportedFileValidationException mException;

        private ImportFilesAsyncTask(@NonNull final Context context,
                @NonNull final Intent intent,
                @NonNull final Callback<ImageMultiPageDocument> callback) {
            mContext = context;
            mIntent = intent;
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
                        final Uri localUri = GiniVision.getInstance().internal().getImageDiskStore()
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
}
