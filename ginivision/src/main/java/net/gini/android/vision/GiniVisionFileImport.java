package net.gini.android.vision;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.util.ActivityHelper;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.review.ReviewActivity;
import net.gini.android.vision.review.multipage.MultiPageReviewActivity;
import net.gini.android.vision.util.CancellationToken;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.NoOpCancellationToken;
import net.gini.android.vision.util.UriHelper;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * This class contains methods for preparing launching the Gini Vision Library with a file received
 * from another app.
 */
public final class GiniVisionFileImport {

    @NonNull
    private final GiniVision mGiniVision;

    /**
     * Screen API
     *
     * <p> When your application receives a file from another application you can use this method to
     * create an Intent for launching the Gini Vision Library.
     *
     * <p> Start the Intent with {@link android.app.Activity#startActivityForResult(Intent, int)} to
     * receive the {@link GiniVisionError} in case there was an error.
     *
     * @param intent                the Intent your app received
     * @param context               Android context
     * @param reviewActivityClass   the class of your application's {@link ReviewActivity} subclass
     * @param analysisActivityClass the class of your application's {@link AnalysisActivity}
     *                              subclass
     *
     * @return an Intent for launching the Gini Vision Library
     *
     * @throws ImportedFileValidationException if the file didn't pass validation
     * @throws IllegalArgumentException        if the Intent's data is not valid or the mime type is
     *                                         not supported
     * @Deprecated Use {@link GiniVision#createIntentForImportedFile(Intent, Context, Class, Class)}
     * instead.
     */
    @Deprecated
    @NonNull
    public static Intent createIntentForImportedFile(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final Class<? extends ReviewActivity> reviewActivityClass,
            @NonNull final Class<? extends AnalysisActivity> analysisActivityClass)
            throws ImportedFileValidationException {
        final Document document = createDocumentForImportedFile(intent, context);
        return createIntentForSingleDocument(context, reviewActivityClass, analysisActivityClass,
                document);
    }

    @NonNull
    private static Intent createIntentForSingleDocument(@NonNull final Context context,
            @NonNull final Class<? extends ReviewActivity> reviewActivityClass,
            @NonNull final Class<? extends AnalysisActivity> analysisActivityClass,
            final Document document) {
        final Intent giniVisionIntent;
        if (document.isReviewable()) {
            giniVisionIntent = createReviewActivityIntent(context, reviewActivityClass,
                    analysisActivityClass, document);
        } else {
            giniVisionIntent = new Intent(context, analysisActivityClass);
            giniVisionIntent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, document);
            giniVisionIntent.setExtrasClassLoader(GiniVisionFileImport.class.getClassLoader());
        }
        return giniVisionIntent;
    }

    @NonNull
    private static Intent createReviewActivityIntent(@NonNull final Context context,
            @Nullable final Class<? extends ReviewActivity> reviewActivityClass,
            @Nullable final Class<? extends AnalysisActivity> analysisActivityClass,
            final Document document) {
        final Intent giniVisionIntent;
        giniVisionIntent = new Intent(context, getReviewActivityClass(reviewActivityClass));
        giniVisionIntent.putExtra(ReviewActivity.EXTRA_IN_DOCUMENT, document);
        giniVisionIntent.setExtrasClassLoader(GiniVisionFileImport.class.getClassLoader());
        ActivityHelper.setActivityExtra(giniVisionIntent,
                ReviewActivity.EXTRA_IN_ANALYSIS_ACTIVITY, context,
                getAnalysisActivityClass(analysisActivityClass));
        return giniVisionIntent;
    }

    @NonNull
    private static Class<? extends ReviewActivity> getReviewActivityClass(
            @Nullable final Class<? extends ReviewActivity> reviewActivityClass) {
        return reviewActivityClass == null ? ReviewActivity.class : reviewActivityClass;
    }

    @NonNull
    private static Class<? extends AnalysisActivity> getAnalysisActivityClass(
            @Nullable final Class<? extends AnalysisActivity> analysisActivityClass) {
        return analysisActivityClass == null ? AnalysisActivity.class : analysisActivityClass;
    }

    /**
     * Component API
     *
     * <p> When your application receives a file from another application you can use this method to
     * create a Document for launching one of the Gini Vision Library's Review Fragments or Analysis
     * Fragments.
     *
     * <p> If the Document can be reviewed ({@link Document#isReviewable()}) launch one of the
     * Review Fragments ({@link net.gini.android.vision.review.ReviewFragmentCompat} or {@link
     * net.gini.android.vision.review.ReviewFragmentStandard}).
     *
     * <p> If the Document cannot be reviewed you must launch one of the Analysis Fragments ({@link
     * net.gini.android.vision.analysis.AnalysisFragmentCompat} or {@link
     * net.gini.android.vision.analysis.AnalysisFragmentStandard}).
     *
     * @param intent  the Intent your app received
     * @param context Android context
     *
     * @return a Document for launching one of the Gini Vision Library's Review Fragments or
     * Analysis Fragments
     *
     * @throws ImportedFileValidationException if the file didn't pass validation
     * @Deprecated Use {@link GiniVision#createDocumentForImportedFile(Intent, Context)} instead.
     */
    @Deprecated
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

    GiniVisionFileImport(@NonNull final GiniVision giniVision) {
        mGiniVision = giniVision;
    }

    CancellationToken createIntentForImportedFiles(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final AsyncCallback<Intent, ImportedFileValidationException> callback) {
        final CancellationToken cancellationToken =
                createDocumentForImportedFiles(intent, context,
                        new AsyncCallback<Document, ImportedFileValidationException>() {
                            @Override
                            public void onSuccess(final Document result) {
                                final Intent giniVisionIntent;
                                if (result.getType() == Document.Type.IMAGE_MULTI_PAGE) {
                                    // The new ImageMultiPageDocument was already added to the memory store
                                    giniVisionIntent = MultiPageReviewActivity.createIntent(
                                            context);
                                } else {
                                    giniVisionIntent = createIntentForSingleDocument(context,
                                            ReviewActivity.class, AnalysisActivity.class,
                                            result);
                                }
                                callback.onSuccess(giniVisionIntent);
                            }

                            @Override
                            public void onError(final ImportedFileValidationException exception) {
                                callback.onError(exception);
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

    CancellationToken createDocumentForImportedFiles(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final AsyncCallback<Document, ImportedFileValidationException> callback) {
        if (!GiniVision.hasInstance()) {
            callback.onError(createNoGiniVisionFileValidationException());
            return new NoOpCancellationToken();
        }
        final List<Uri> uris = IntentHelper.getUris(intent);
        if (uris == null) {
            callback.onError(
                    new ImportedFileValidationException("Intent data did not contain Uris"));
            return new NoOpCancellationToken();
        }
        if (uris.size() == 1 && UriHelper.hasMimeType(uris.get(0), context,
                MimeType.APPLICATION_PDF.asString())) {
            try {
                final Document document = createDocumentForImportedFile(intent,
                        context);
                callback.onSuccess(document);
            } catch (final ImportedFileValidationException e) {
                callback.onError(e);
            }
            return new NoOpCancellationToken();
        } else {
            final ImportImageFileUrisAsyncTask asyncTask = new ImportImageFileUrisAsyncTask(context,
                    intent,
                    mGiniVision, Document.Source.newExternalSource(),
                    Document.ImportMethod.OPEN_WITH,
                    new AsyncCallback<ImageMultiPageDocument, ImportedFileValidationException>() {
                        @Override
                        public void onSuccess(final ImageMultiPageDocument result) {
                            if (!GiniVision.hasInstance()) {
                                callback.onError(createNoGiniVisionFileValidationException());
                                return;
                            }
                            GiniVision.getInstance().internal()
                                    .getImageMultiPageDocumentMemoryStore()
                                    .setMultiPageDocument(result);
                            callback.onSuccess(result);
                        }

                        @Override
                        public void onError(final ImportedFileValidationException exception) {
                            callback.onError(exception);
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
                }
            };
        }
    }

    @NonNull
    private static ImportedFileValidationException createNoGiniVisionFileValidationException() {
        return new ImportedFileValidationException(
                "Cannot import files. GiniVision instance not available. Create it with GiniVision.newInstance().");
    }

}
