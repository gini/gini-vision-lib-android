package net.gini.android.vision;

import static net.gini.android.vision.internal.util.FeatureConfiguration.isMultiPageEnabled;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import net.gini.android.vision.analysis.AnalysisActivity;
import net.gini.android.vision.camera.CameraActivity;
import net.gini.android.vision.document.DocumentFactory;
import net.gini.android.vision.document.ImageDocument;
import net.gini.android.vision.document.ImageMultiPageDocument;
import net.gini.android.vision.internal.util.ActivityHelper;
import net.gini.android.vision.internal.util.DeviceHelper;
import net.gini.android.vision.internal.util.FileImportValidator;
import net.gini.android.vision.internal.util.MimeType;
import net.gini.android.vision.network.GiniVisionNetworkService;
import net.gini.android.vision.review.ReviewActivity;
import net.gini.android.vision.review.multipage.MultiPageReviewActivity;
import net.gini.android.vision.util.IntentHelper;
import net.gini.android.vision.util.UriHelper;

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
     *
     * @deprecated Use {@link GiniVisionFileImport#createIntentForImportedFile(Intent, Context)} instead
     * when a {@link GiniVision} instance is available. The document
     * is analyzed internally by using the configured {@link GiniVisionNetworkService}
     * implementation. The extractions will be returned in the extra called
     * {@link CameraActivity#EXTRA_OUT_EXTRACTIONS} of the {@link CameraActivity}'s result Intent.
     */
    @Deprecated
    @NonNull
    public static Intent createIntentForImportedFile(@NonNull final Intent intent,
            @NonNull final Context context,
            @NonNull final Class<? extends ReviewActivity> reviewActivityClass,
            @NonNull final Class<? extends AnalysisActivity> analysisActivityClass)
            throws ImportedFileValidationException {
        final Document document = createDocumentForImportedFile(intent, context);
        final Intent giniVisionIntent;
        if (document.getType() == Document.Type.IMAGE_MULTI_PAGE) {
            final ImageMultiPageDocument multiPageDocument = (ImageMultiPageDocument) document;
            final List<ImageDocument> imageDocuments = multiPageDocument.getDocuments();
            if (imageDocuments.size() > 1) {
                giniVisionIntent = MultiPageReviewActivity.createIntent(context);
            } else {
                final ImageDocument imageDocument = imageDocuments.get(0);
                giniVisionIntent = createReviewActivityIntent(context, ReviewActivity.class,
                        AnalysisActivity.class, imageDocument);
            }
        } else if (document.isReviewable()) {
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
     * @return an Intent for launching the Gini Vision Library
     * @throws ImportedFileValidationException if the file didn't pass validation
     * @throws IllegalArgumentException        if the Intent's data is not valid or the mime type is not
     *                                         supported
     */
    @NonNull
    public static Intent createIntentForImportedFile(@NonNull final Intent intent,
            @NonNull final Context context)
            throws ImportedFileValidationException {
        final Document document = createDocumentForImportedFile(intent, context);
        final Intent giniVisionIntent;
        if (document.getType() == Document.Type.IMAGE_MULTI_PAGE) {
            final ImageMultiPageDocument multiPageDocument = (ImageMultiPageDocument) document;
            final List<ImageDocument> imageDocuments = multiPageDocument.getDocuments();
            if (imageDocuments.size() > 1) {
                giniVisionIntent = MultiPageReviewActivity.createIntent(context);
            } else {
                final ImageDocument imageDocument = imageDocuments.get(0);
                giniVisionIntent = createReviewActivityIntent(context, ReviewActivity.class,
                        AnalysisActivity.class, imageDocument);
            }
        } else {
            if (document.isReviewable()) {
                giniVisionIntent = createReviewActivityIntent(context, ReviewActivity.class,
                        AnalysisActivity.class, document);
            } else {
                giniVisionIntent = new Intent(context, AnalysisActivity.class);
                giniVisionIntent.putExtra(AnalysisActivity.EXTRA_IN_DOCUMENT, document);
            }
        }
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
     * @throws IllegalArgumentException        if the Intent's data is null or the mime type is not
     *                                         supported
     */
    @NonNull
    public static Document createDocumentForImportedFile(@NonNull final Intent intent,
            @NonNull final Context context) throws ImportedFileValidationException {
        if (isMultiPageEnabled() && IntentHelper.hasMultipleUris(intent)) {
            return createDocumentForImportedFiles(intent, context);
        }
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

    @NonNull
    private static Document createDocumentForImportedFiles(@NonNull final Intent intent,
            @NonNull final Context context) throws ImportedFileValidationException {
        if (!GiniVision.hasInstance()) {
            throw new IllegalStateException(
                    "Cannot import files. GiniVision instance not available. Create it with GiniVision.newInstance().");
        }
        final List<Uri> uris = IntentHelper.getUris(intent);
        if (uris == null) {
            throw new ImportedFileValidationException("Intent data did not contain Uris");
        }
        final ImageMultiPageDocument multiPageDocument = new ImageMultiPageDocument(
                Document.Source.newExternalSource(), Document.ImportMethod.OPEN_WITH);
        for (final Uri uri : uris) {
            if (!UriHelper.isUriInputStreamAvailable(uri, context)) {
                throw new ImportedFileValidationException(
                        "InputStream not available for one of the Intent's data Uris");
            }
            final FileImportValidator fileImportValidator = new FileImportValidator(context);
            if (fileImportValidator.matchesCriteria(uri)) {
                if (IntentHelper.hasMimeTypeWithPrefix(uri, context,
                        MimeType.IMAGE_PREFIX.asString())) {
                    final ImageDocument document = DocumentFactory.newImageDocumentFromUri(uri,
                            intent, context, DeviceHelper.getDeviceOrientation(context),
                            DeviceHelper.getDeviceType(context), Document.ImportMethod.OPEN_WITH);
                    multiPageDocument.addDocument(document);
                }
            } else {
                throw new ImportedFileValidationException(fileImportValidator.getError());
            }
        }
        if (multiPageDocument.getDocuments().isEmpty()) {
            throw new ImportedFileValidationException("Intent did not contain images");
        }
        GiniVision.getInstance().internal().getImageMultiPageDocumentMemoryStore()
                .setMultiPageDocument(multiPageDocument);
        return multiPageDocument;
    }

    private GiniVisionFileImport() {
    }
}
